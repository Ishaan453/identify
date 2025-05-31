package com.bitespeed.service;

import com.bitespeed.model.Contact;
import com.bitespeed.model.IdentifyResponse;
import com.bitespeed.util.DatabaseUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IdentifyService {

    public IdentifyResponse identify(String email, String phoneNumber) throws SQLException {
        // Step 1: Find existing contacts with matching email or phone number
        List<Contact> matchingContacts = DatabaseUtil.findContacts(email, phoneNumber);

        // Step 2: If no matching contacts, create a new primary contact
        if (matchingContacts.isEmpty()) {
            Contact newContact = DatabaseUtil.insertContact(email, phoneNumber, null, "primary");
            return new IdentifyResponse(
                new IdentifyResponse.ContactResponse(
                    newContact.getId(),
                    email != null ? List.of(email) : List.of(),
                    phoneNumber != null ? List.of(phoneNumber) : List.of(),
                    List.of()
                )
            );
        }

        // Step 3: Find the primary contact (oldest primary or trace back from oldest secondary)
        Contact primaryContact = findPrimaryContact(matchingContacts);

        // Step 4: Check if we need to create a new secondary contact
        boolean newInfo = false;
        boolean emailExists = matchingContacts.stream().anyMatch(c -> email != null && email.equals(c.getEmail()));
        boolean phoneExists = matchingContacts.stream().anyMatch(c -> phoneNumber != null && phoneNumber.equals(c.getPhoneNumber()));

        if ((email != null && !emailExists) || (phoneNumber != null && !phoneExists)) {
            newInfo = true;
        }

        if (newInfo) {
            DatabaseUtil.insertContact(email, phoneNumber, primaryContact.getId(), "secondary");
        }

        // Step 5: Handle case where primary contacts need to be merged
        for (Contact contact : matchingContacts) {
            if ("primary".equals(contact.getLinkPrecedence()) && contact.getId() != primaryContact.getId()) {
                DatabaseUtil.updateContactToSecondary(contact.getId(), primaryContact.getId());
            }
        }

        // Step 6: Fetch all linked contacts and build the response
        List<Contact> linkedContacts = DatabaseUtil.getLinkedContacts(primaryContact.getId());
        Set<String> emails = new HashSet<>();
        Set<String> phoneNumbers = new HashSet<>();
        List<Integer> secondaryContactIds = new ArrayList<>();

        emails.add(primaryContact.getEmail());
        phoneNumbers.add(primaryContact.getPhoneNumber());

        for (Contact contact : linkedContacts) {
            if (contact.getId() != primaryContact.getId()) {
                secondaryContactIds.add(contact.getId());
                if (contact.getEmail() != null) emails.add(contact.getEmail());
                if (contact.getPhoneNumber() != null) phoneNumbers.add(contact.getPhoneNumber());
            }
        }

        return new IdentifyResponse(
            new IdentifyResponse.ContactResponse(
                primaryContact.getId(),
                new ArrayList<>(emails),
                new ArrayList<>(phoneNumbers),
                secondaryContactIds
            )
        );
    }

    private Contact findPrimaryContact(List<Contact> matchingContacts) throws SQLException {
        // Look for primary contacts
        Contact oldestPrimary = null;
        for (Contact contact : matchingContacts) {
            if ("primary".equals(contact.getLinkPrecedence())) {
                if (oldestPrimary == null || contact.getCreatedAt().isBefore(oldestPrimary.getCreatedAt())) {
                    oldestPrimary = contact;
                }
            }
        }

        // If a primary contact is found, return it
        if (oldestPrimary != null) {
            return oldestPrimary;
        }

        // If no primary contact, find the oldest secondary and trace back to its primary
        Contact oldestSecondary = matchingContacts.get(0);
        for (Contact contact : matchingContacts) {
            if (contact.getCreatedAt().isBefore(oldestSecondary.getCreatedAt())) {
                oldestSecondary = contact;
            }
        }

        // Fetch the primary contact using the linkedId of the oldest secondary
        int linkedId = oldestSecondary.getLinkedId();
        List<Contact> linkedContacts = DatabaseUtil.getLinkedContacts(linkedId);
        for (Contact contact : linkedContacts) {
            if (contact.getId() == linkedId) {
                return contact;
            }
        }

        // Fallback to the oldest secondary if no primary is found
        return oldestSecondary;
    }
}
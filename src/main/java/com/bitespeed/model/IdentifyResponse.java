package com.bitespeed.model;

import java.util.List;

public class IdentifyResponse {
    private ContactResponse contact;

    public IdentifyResponse(ContactResponse contact) {
        this.contact = contact;
    }

    public ContactResponse getContact() { return contact; }
    public void setContact(ContactResponse contact) { this.contact = contact; }

    public static class ContactResponse {
        private int primaryContactId;
        private List<String> emails;
        private List<String> phoneNumbers;
        private List<Integer> secondaryContactIds;

        public ContactResponse(int primaryContactId, List<String> emails, List<String> phoneNumbers, 
                               List<Integer> secondaryContactIds) {
            this.primaryContactId = primaryContactId;
            this.emails = emails;
            this.phoneNumbers = phoneNumbers;
            this.secondaryContactIds = secondaryContactIds;
        }

        public int getPrimaryContactId() { return primaryContactId; }
        public List<String> getEmails() { return emails; }
        public List<String> getPhoneNumbers() { return phoneNumbers; }
        public List<Integer> getSecondaryContactIds() { return secondaryContactIds; }
    }
}
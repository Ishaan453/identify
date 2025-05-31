package com.bitespeed.util;

import com.bitespeed.model.Contact;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUtil {
    private static final String DB_URL = "jdbc:mysql://bite-speed.c368cy40etha.ap-south-1.rds.amazonaws.com:3306/bitespeed";
    private static final String DB_USER = "bitespeed";
    private static final String DB_PASSWORD = "bitespeed";

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // Find contacts by email or phone number
    public static List<Contact> findContacts(String email, String phoneNumber) throws SQLException {
        List<Contact> contacts = new ArrayList<>();
        String query = "SELECT * FROM Contact WHERE (email = ? OR phoneNumber = ?) AND deletedAt IS NULL";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email != null ? email : "");
            stmt.setString(2, phoneNumber != null ? phoneNumber : "");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                contacts.add(new Contact(
                    rs.getInt("id"),
                    rs.getString("phoneNumber"),
                    rs.getString("email"),
                    rs.getObject("linkedId") != null ? rs.getInt("linkedId") : null,
                    rs.getString("linkPrecedence"),
                    rs.getTimestamp("createdAt").toLocalDateTime(),
                    rs.getTimestamp("updatedAt").toLocalDateTime(),
                    rs.getTimestamp("deletedAt") != null ? rs.getTimestamp("deletedAt").toLocalDateTime() : null
                ));
            }
        }
        return contacts;
    }

    // Insert a new contact
    public static Contact insertContact(String email, String phoneNumber, Integer linkedId, String linkPrecedence) 
        throws SQLException {
    LocalDateTime now = LocalDateTime.now();
    String insertQuery = "INSERT INTO Contact (phoneNumber, email, linkedId, linkPrecedence, createdAt, updatedAt) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
        stmt.setString(1, phoneNumber);
        stmt.setString(2, email);
        stmt.setObject(3, linkedId);
        stmt.setString(4, linkPrecedence);
        stmt.setTimestamp(5, Timestamp.valueOf(now));
        stmt.setTimestamp(6, Timestamp.valueOf(now));

        // Execute the INSERT statement
        stmt.executeUpdate();

        // Get the generated ID
        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            int generatedId = rs.getInt(1);

            // Fetch the inserted row
            String selectQuery = "SELECT * FROM Contact WHERE id = ?";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
                selectStmt.setInt(1, generatedId);
                ResultSet selectRs = selectStmt.executeQuery();
                if (selectRs.next()) {
                    return new Contact(
                        selectRs.getInt("id"),
                        selectRs.getString("phoneNumber"),
                        selectRs.getString("email"),
                        selectRs.getObject("linkedId") != null ? selectRs.getInt("linkedId") : null,
                        selectRs.getString("linkPrecedence"),
                        selectRs.getTimestamp("createdAt").toLocalDateTime(),
                        selectRs.getTimestamp("updatedAt").toLocalDateTime(),
                        selectRs.getTimestamp("deletedAt") != null ? selectRs.getTimestamp("deletedAt").toLocalDateTime() : null
                    );
                }
            }
        }
    }
    return null;
}

    // Update a contact to make it secondary
    public static void updateContactToSecondary(int contactId, int linkedId) throws SQLException {
        LocalDateTime now = LocalDateTime.now();
        String query = "UPDATE Contact SET linkedId = ?, linkPrecedence = 'secondary', updatedAt = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, linkedId);
            stmt.setTimestamp(2, Timestamp.valueOf(now));
            stmt.setInt(3, contactId);
            stmt.executeUpdate(); 
        }
    }

    // Fetch all contacts linked to a primary contact
    public static List<Contact> getLinkedContacts(int primaryId) throws SQLException {
        List<Contact> contacts = new ArrayList<>();
        String query = "SELECT * FROM Contact WHERE (id = ? OR linkedId = ?) AND deletedAt IS NULL";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, primaryId);
            stmt.setInt(2, primaryId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                contacts.add(new Contact(
                    rs.getInt("id"),
                    rs.getString("phoneNumber"),
                    rs.getString("email"),
                    rs.getObject("linkedId") != null ? rs.getInt("linkedId") : null,
                    rs.getString("linkPrecedence"),
                    rs.getTimestamp("createdAt").toLocalDateTime(),
                    rs.getTimestamp("updatedAt").toLocalDateTime(),
                    rs.getTimestamp("deletedAt") != null ? rs.getTimestamp("deletedAt").toLocalDateTime() : null
                ));
            }
        }
        return contacts;
    }
}
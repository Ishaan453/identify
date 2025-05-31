package com.bitespeed.model;

import java.time.LocalDateTime;

public class Contact {
    private int id;
    private String phoneNumber;
    private String email;
    private Integer linkedId; // Nullable, so using Integer
    private String linkPrecedence; // "primary" or "secondary"
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    // Constructor
    public Contact(int id, String phoneNumber, String email, Integer linkedId, String linkPrecedence, 
                   LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.linkedId = linkedId;
        this.linkPrecedence = linkPrecedence;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getLinkedId() { return linkedId; }
    public void setLinkedId(Integer linkedId) { this.linkedId = linkedId; }
    public String getLinkPrecedence() { return linkPrecedence; }
    public void setLinkPrecedence(String linkPrecedence) { this.linkPrecedence = linkPrecedence; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}
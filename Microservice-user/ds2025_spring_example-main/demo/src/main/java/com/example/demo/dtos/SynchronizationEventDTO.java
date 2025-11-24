package com.example.demo.dtos;

import java.io.Serializable;
import java.util.UUID;

public class SynchronizationEventDTO implements Serializable {
    
    // Serializable ID for compatibility
    private static final long serialVersionUID = 1L; 

    private UUID id;
    private String entityType; // e.g., "USER", "DEVICE"
    private String action;     // e.g., "CREATED", "UPDATED", "DELETED"
    private String name;       
    private String email;   
    private String password;   

    // --- Constructors ---
    
    public SynchronizationEventDTO() {}

    public SynchronizationEventDTO(UUID id, String entityType, String action, String name, String email, String password) {
        this.id = id;
        this.entityType = entityType;
        this.action = action;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // --- Getters and Setters ---

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "SynchronizationEventDTO{" +
                "id=" + id +
                ", entityType='" + entityType + '\'' +
                ", action='" + action + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
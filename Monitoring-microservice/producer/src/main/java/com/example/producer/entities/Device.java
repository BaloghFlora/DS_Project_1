package com.example.producer.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "synced_device")
public class Device {

    @Id
    private UUID id;
    
    // We only need the ID for simulation, but mapping the name is good practice
    private String deviceName;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
}
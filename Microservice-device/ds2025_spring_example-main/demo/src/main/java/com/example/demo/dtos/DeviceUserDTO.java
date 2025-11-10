package com.example.demo.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public class DeviceUserDTO {
    private UUID id;
    private UUID deviceId;
    private String deviceName;
    private UUID userId;
    private String username;
    private LocalDateTime assignedDate;

    public DeviceUserDTO() {}

    public DeviceUserDTO(UUID id, UUID deviceId, String deviceName, UUID userId, String username, LocalDateTime assignedDate) {
        this.id = id;
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.userId = userId;
        this.username = username;
        this.assignedDate = assignedDate;
    }

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getDeviceId() { return deviceId; }
    public void setDeviceId(UUID deviceId) { this.deviceId = deviceId; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public LocalDateTime getAssignedDate() { return assignedDate; }
    public void setAssignedDate(LocalDateTime assignedDate) { this.assignedDate = assignedDate; }
}
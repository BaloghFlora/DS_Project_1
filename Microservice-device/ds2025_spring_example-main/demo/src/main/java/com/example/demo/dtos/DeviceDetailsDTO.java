package com.example.demo.dtos;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

public class DeviceDetailsDTO {

    private UUID id;

    @NotBlank(message = "Device name is required")
    private String deviceName;

    // No longer a single user ID — a device can have multiple users
    private List<UUID> userIds = new java.util.ArrayList<>();

    @NotBlank(message = "Device status is required")
    private String deviceStatus;

    public DeviceDetailsDTO() {}

    public DeviceDetailsDTO(UUID id, String deviceName, String deviceStatus, List<UUID> userIds) {
        this.id = id;
        this.deviceName = deviceName;
        this.deviceStatus = deviceStatus;
        this.userIds = userIds;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public List<UUID> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<UUID> userIds) {
        this.userIds = userIds;
    }

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }
}

package com.example.demo.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class DeviceDetailsDTO {

    private UUID id;

    @NotBlank(message = "Device name is required")
    private String deviceName;

    @NotNull(message = "Device user ID is required")
    private UUID deviceUserId;

    @NotBlank(message = "Device status is required")
    private String deviceStatus;

    public DeviceDetailsDTO() {}

    public DeviceDetailsDTO(UUID id, String deviceName, UUID deviceUserId, String deviceStatus) {
        this.id = id;
        this.deviceName = deviceName;
        this.deviceUserId = deviceUserId;
        this.deviceStatus = deviceStatus;
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

    public UUID getDeviceUserId() {
        return deviceUserId;
    }

    public void setDeviceUserId(UUID deviceUserId) {
        this.deviceUserId = deviceUserId;
    }

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }
}

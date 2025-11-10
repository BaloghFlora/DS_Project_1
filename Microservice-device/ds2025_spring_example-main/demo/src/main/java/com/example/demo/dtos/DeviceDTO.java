package com.example.demo.dtos;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class DeviceDTO {

    private UUID id;
    private String deviceName;
    private String deviceStatus;

    // Optional: include the IDs of assigned users
    private List<UUID> userIds;

    public DeviceDTO() {
    }

    public DeviceDTO(UUID id, String deviceName, String deviceStatus) {
        this.id = id;
        this.deviceName = deviceName;
        this.deviceStatus = deviceStatus;
    }

    public DeviceDTO(UUID id, String deviceName, String deviceStatus, List<UUID> userIds) {
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

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public List<UUID> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<UUID> userIds) {
        this.userIds = userIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeviceDTO that)) return false;
        return Objects.equals(deviceName, that.deviceName) &&
               Objects.equals(deviceStatus, that.deviceStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceName, deviceStatus);
    }
}

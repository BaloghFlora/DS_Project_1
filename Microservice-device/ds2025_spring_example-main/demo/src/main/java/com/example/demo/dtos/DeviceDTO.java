package com.example.demo.dtos;

import java.util.Objects;
import java.util.UUID;

public class DeviceDTO {

    private UUID id;
    private String deviceName;
    private UUID deviceUserId;
    private String deviceStatus;

    public DeviceDTO() {
    }

    public DeviceDTO(UUID id, String deviceName, UUID deviceUserId, String deviceStatus) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceDTO that = (DeviceDTO) o;
        return Objects.equals(deviceName, that.deviceName) &&
                Objects.equals(deviceUserId, that.deviceUserId) &&
                Objects.equals(deviceStatus, that.deviceStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceName, deviceUserId, deviceStatus);
    }
}

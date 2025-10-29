package com.example.demo.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "device")
public class Device implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "device_name", nullable = false)
    private String deviceName;

    // This is NOT a foreign key, just a UUID reference to a user
    @Column(name = "device_user_id", nullable = false)
    private UUID deviceUserId;

    @Column(name = "device_status", nullable = false)
    private String deviceStatus;

    public Device() {
    }

    public Device(String deviceName, UUID deviceUserId, String deviceStatus) {
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

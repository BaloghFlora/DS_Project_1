package com.example.consumer.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.UUID;

@Entity
@Table(name = "synced_device")
public class Device {
    @Id
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id; 

    @Column(nullable = false)
    private String deviceName;

    public Device() {}

    public Device(UUID id, String deviceName) {
        this.id = id;
        this.deviceName = deviceName;
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
}
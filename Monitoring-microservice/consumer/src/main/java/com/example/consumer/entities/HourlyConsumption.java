package com.example.consumer.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import java.time.LocalDateTime;

@Entity
public class HourlyConsumption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String deviceId; 

    @Column(nullable = false)
    private LocalDateTime hourTimestamp; 

    @Column(nullable = false)
    private Double consumptionKWh; 

    public HourlyConsumption() {}

    public HourlyConsumption(String deviceId, LocalDateTime hourTimestamp, Double consumptionKWh) {
        this.deviceId = deviceId;
        this.hourTimestamp = hourTimestamp;
        this.consumptionKWh = consumptionKWh;
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public LocalDateTime getHourTimestamp() { return hourTimestamp; }
    public void setHourTimestamp(LocalDateTime hourTimestamp) { this.hourTimestamp = hourTimestamp; }
    public Double getConsumptionKWh() { return consumptionKWh; }
    public void setConsumptionKWh(Double consumptionKWh) { this.consumptionKWh = consumptionKWh; }
}
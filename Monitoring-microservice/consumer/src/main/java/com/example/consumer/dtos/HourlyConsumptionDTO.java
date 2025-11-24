package com.example.consumer.dtos;

import java.time.LocalDateTime;

public class HourlyConsumptionDTO {
    private String deviceId;
    private LocalDateTime hour;
    private Double consumption;

    public HourlyConsumptionDTO() {}

    public HourlyConsumptionDTO(String deviceId, LocalDateTime hour, Double consumption) {
        this.deviceId = deviceId;
        this.hour = hour;
        this.consumption = consumption;
    }

    // --- Getters and Setters ---
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public LocalDateTime getHour() { return hour; }
    public void setHour(LocalDateTime hour) { this.hour = hour; }
    public Double getConsumption() { return consumption; }
    public void setConsumption(Double consumption) { this.consumption = consumption; }
}
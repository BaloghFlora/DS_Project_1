package com.example.consumer.controllers;

import com.example.consumer.dtos.HourlyConsumptionDTO;
import com.example.consumer.services.MonitoringService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/monitoring")
public class MonitoringController {

    private final MonitoringService monitoringService;

    public MonitoringController(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    /**
     * Endpoint to retrieve hourly energy consumption for a specific device and day.
     * Only users (ROLE_USER) assigned to the device can access this data.
     * Access control (ensuring the user owns the device) is typically done in the Device Microservice/API Gateway 
     * but for this level, we assume the provided JWT grants base role access.
     * @param deviceId The ID of the device to query.
     * @param day The date (YYYY-MM-DD) for the data.
     * @return List of HourlyConsumptionDTOs.
     */
    @GetMapping("/consumption")
    @PreAuthorize("hasRole('ROLE_USER')") 
    public ResponseEntity<List<HourlyConsumptionDTO>> getConsumptionByDeviceAndDay(
            @RequestParam String deviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day) {

        List<HourlyConsumptionDTO> consumptionData = monitoringService.getHourlyConsumption(deviceId, day);
        return ResponseEntity.ok(consumptionData);
    }
}
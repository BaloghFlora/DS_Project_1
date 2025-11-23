package com.example.consumer.services;

import com.example.consumer.dtos.HourlyConsumptionDTO;
import com.example.consumer.entities.HourlyConsumption;
import com.example.consumer.repositories.HourlyConsumptionRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MonitoringService {

    private final HourlyConsumptionRepository hourlyConsumptionRepository;

    public MonitoringService(HourlyConsumptionRepository hourlyConsumptionRepository) {
        this.hourlyConsumptionRepository = hourlyConsumptionRepository;
    }

    /**
     * Retrieves aggregated hourly consumption data for a specific device and day.
     * @param deviceId The ID of the device.
     * @param day The date to query.
     * @return A list of HourlyConsumptionDTOs.
     */
    public List<HourlyConsumptionDTO> getHourlyConsumption(String deviceId, LocalDate day) {
        LocalDateTime startOfDay = day.atStartOfDay();
        LocalDateTime endOfDay = day.plusDays(1).atStartOfDay().minusNanos(1);

        List<HourlyConsumption> results = hourlyConsumptionRepository.findByDeviceIdAndDay(
                deviceId,
                startOfDay,
                endOfDay
        );

        return results.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private HourlyConsumptionDTO toDTO(HourlyConsumption entity) {
        return new HourlyConsumptionDTO(
                entity.getDeviceId(),
                entity.getHourTimestamp(),
                entity.getConsumptionKWh()
        );
    }
}
package com.example.consumer.repositories;

import com.example.consumer.entities.HourlyConsumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface HourlyConsumptionRepository extends JpaRepository<HourlyConsumption, Long> {

    /**
     * Finds hourly consumption records for a given device within a specific day range.
     * @param deviceId The UUID of the device (stored as String).
     * @param startOfDay The start of the day (e.g., YYYY-MM-DD 00:00:00).
     * @param endOfDay The end of the day (e.g., YYYY-MM-DD 23:59:59.999...).
     * @return List of HourlyConsumption records sorted by hour.
     */
    @Query("SELECT h FROM HourlyConsumption h " +
           "WHERE h.deviceId = :deviceId " +
           "AND h.hourTimestamp BETWEEN :startOfDay AND :endOfDay " +
           "ORDER BY h.hourTimestamp ASC")
    List<HourlyConsumption> findByDeviceIdAndDay(
            @Param("deviceId") String deviceId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay);
}
package com.example.demo.repositories;

import com.example.demo.entities.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, UUID> {

    /**
     * Example: JPA generates query based on field name.
     * Finds all devices belonging to a specific user.
     */
    List<Device> findByDeviceUserId(UUID deviceUserId);

    /**
     * Example: Custom query to find active devices by user ID.
     */
    @Query("SELECT d FROM Device d WHERE d.deviceUserId = :userId AND d.deviceStatus = 'ACTIVE'")
    Optional<List<Device>> findActiveDevicesByUserId(@Param("userId") UUID userId);
}

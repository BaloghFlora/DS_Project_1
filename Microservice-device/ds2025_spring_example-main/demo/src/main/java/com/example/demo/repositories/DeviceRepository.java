package com.example.demo.repositories;

import com.example.demo.entities.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, UUID> {

    // Find all devices assigned to a specific user
    @Query("SELECT du.device FROM DeviceUser du WHERE du.user.id = :userId")
    List<Device> findDevicesByUserId(@Param("userId") UUID userId);

    // Find active devices of a user
    @Query("SELECT du.device FROM DeviceUser du WHERE du.user.id = :userId AND du.device.deviceStatus = 'ACTIVE'")
    List<Device> findActiveDevicesByUserId(@Param("userId") UUID userId);
}

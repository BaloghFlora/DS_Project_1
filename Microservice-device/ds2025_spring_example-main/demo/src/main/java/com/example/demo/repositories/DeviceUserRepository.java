package com.example.demo.repositories;

import com.example.demo.entities.DeviceUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DeviceUserRepository extends JpaRepository<DeviceUser, UUID> {

    List<DeviceUser> findByDeviceId(UUID deviceId);

    List<DeviceUser> findByUserId(UUID userId);

    void deleteByDeviceIdAndUserId(UUID deviceId, UUID userId);
}

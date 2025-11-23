package com.example.demo.services;

import com.example.demo.dtos.DeviceDTO;
import com.example.demo.dtos.DeviceDetailsDTO;
import com.example.demo.dtos.builders.DeviceBuilder;
import com.example.demo.entities.Device;
import com.example.demo.entities.DeviceUser;
import com.example.demo.entities.User;
import com.example.demo.handlers.exceptions.model.ResourceNotFoundException;
import com.example.demo.repositories.DeviceRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.repositories.DeviceUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.Collections;


import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeviceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceService.class);
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final DeviceUserRepository deviceUserRepository;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository, UserRepository userRepository, DeviceUserRepository deviceUserRepository) {
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
        this.deviceUserRepository = deviceUserRepository;
    }

    public List<DeviceDTO> findDevices() {
        List<Device> deviceList = deviceRepository.findAll();
        return deviceList.stream()
                .map(DeviceBuilder::toDeviceDTO)
                .collect(Collectors.toList());
    }

    public DeviceDetailsDTO findDeviceById(UUID id) {
        Optional<Device> deviceOptional = deviceRepository.findById(id);
        if (deviceOptional.isEmpty()) {
            LOGGER.error("Device with id {} was not found in db", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
        }
        return DeviceBuilder.toDeviceDetailsDTO(deviceOptional.get());
    }

     public UUID insert(DeviceDetailsDTO dto) {
        // Step 1: create and save the device
        Device device = new Device();
        device.setDeviceName(dto.getDeviceName());
        device.setDeviceStatus(dto.getDeviceStatus());
        Device savedDevice = deviceRepository.save(device);

        // Step 2: create entries in UserDevice
        for (UUID userId : dto.getUserIds()) {
            // Optional: validate user exists
            if (!userRepository.existsById(userId)) {
                throw new RuntimeException("User not found: " + userId);
            }

            DeviceUser link = new DeviceUser();
            link.setDevice(savedDevice);
            link.setUser(userRepository.findById(userId).get());
            deviceUserRepository.save(link);
        }
        SynchronizationEventDTO event = new SynchronizationEventDTO(
            savedDevice.getId(), 
            "DEVICE", 
            "CREATED", 
            savedDevice.getDeviceName(), 
            null
        );
        synchronizationService.publishEvent(event);
        
        return savedDevice.getId();
    }
    /**
        * Update an existing device.
     */
    public DeviceDetailsDTO update(UUID id, DeviceDetailsDTO dto) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id));

        device.setDeviceName(dto.getDeviceName());
        device.setDeviceStatus(dto.getDeviceStatus());
        
        // Note: This simple update does not re-assign users. 
        // A more complex implementation would compare dto.getUserIds() with existing links.

        // --- ASYNC: Publish DEVICE UPDATED Event ---
        SynchronizationEventDTO event = new SynchronizationEventDTO(
            updatedDevice.getId(), 
            "DEVICE", 
            "UPDATED", 
            updatedDevice.getDeviceName(), 
            null
        );
        synchronizationService.publishEvent(event);
        Device updatedDevice = deviceRepository.save(device);
        LOGGER.debug("Device with id {} was updated in db", updatedDevice.getId());
        return DeviceBuilder.toDeviceDetailsDTO(updatedDevice);
    }

    
    public void deleteDevice(UUID id) {
        if (!deviceRepository.existsById(id)) {
            LOGGER.error("Attempted to delete non-existent device with id {}", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
        }
        deviceRepository.deleteById(id);
        LOGGER.debug("Device with id {} was deleted from db", id);
        // --- ASYNC: Publish DEVICE DELETED Event ---
        SynchronizationEventDTO event = new SynchronizationEventDTO(
            id, 
            "DEVICE", 
            "DELETED", 
            null, 
            null
        );
        synchronizationService.publishEvent(event);
    }

    /**
     * Get all devices assigned to the currently logged-in user.
     */
    @PreAuthorize("hasRole('ROLE_USER')") // Ensures only users can access this
    public List<DeviceDTO> findDevicesByUsername() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            LOGGER.warn("User with username {} not found in device-service DB", username);
            // This can happen if a user exists in auth-service but not in device-service's user table
            // You must create a matching user in this service's DB via POST /users
            return Collections.emptyList();
        }
        
        UUID userId = userOptional.get().getId();
        List<Device> deviceList = deviceRepository.findDevicesByUserId(userId);
        return deviceList.stream()
                .map(DeviceBuilder::toDeviceDTO)
                .collect(Collectors.toList());
    }
}

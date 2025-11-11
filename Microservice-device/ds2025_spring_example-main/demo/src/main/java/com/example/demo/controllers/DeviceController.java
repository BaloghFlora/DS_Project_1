package com.example.demo.controllers;

import com.example.demo.dtos.DeviceDTO;
import com.example.demo.dtos.DeviceDetailsDTO;
import com.example.demo.services.DeviceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.example.demo.services.DeviceUserService;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/devices")
@Validated
public class DeviceController {

    private final DeviceService deviceService;

    private final DeviceUserService deviceUserService;

    public DeviceController(DeviceService deviceService, DeviceUserService deviceUserService) {
        this.deviceService = deviceService;
        this.deviceUserService = deviceUserService;
    }


    @GetMapping
    public ResponseEntity<List<DeviceDTO>> getDevices() {
        return ResponseEntity.ok(deviceService.findDevices());
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody DeviceDetailsDTO device) {
        UUID id = deviceService.insert(device);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(location).build(); // 201 + Location header
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceDetailsDTO> getDevice(@PathVariable UUID id) {
        return ResponseEntity.ok(deviceService.findDeviceById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable UUID id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @PostMapping("/{deviceId}/users/{userId}")
    public ResponseEntity<Void> assignUserToDevice(
            @PathVariable UUID deviceId,
            @PathVariable UUID userId) {
        deviceUserService.assignDeviceToUser(deviceId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Get all devices for the currently authenticated user (Client role).
     */
    @GetMapping("/my-devices")
    @PreAuthorize("hasRole('ROLE_USER')") // Redundant check, but good practice
    public ResponseEntity<List<DeviceDTO>> getMyDevices() {
        return ResponseEntity.ok(deviceService.findDevicesByUsername());
    }

    /**
     * Update an existing device (Admin role).
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<DeviceDetailsDTO> updateDevice(
            @PathVariable UUID id, 
            @Valid @RequestBody DeviceDetailsDTO deviceDetails) {
        
        DeviceDetailsDTO updatedDevice = deviceService.update(id, deviceDetails);
        return ResponseEntity.ok(updatedDevice);
    }
}

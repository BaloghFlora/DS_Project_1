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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(summary = "Retrieves all devices",
               description = "Fetches a list of all devices with their basic details.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<DeviceDTO>> getDevices() {
        return ResponseEntity.ok(deviceService.findDevices());
    }

    @Operation(summary = "Creates a new device")
    @ApiResponse(responseCode = "200", description = "Successfully created device")
    @ApiResponse(responseCode = "400", description = "Invalid device data supplied")
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> create(@Valid @RequestBody DeviceDetailsDTO device) {
        UUID id = deviceService.insert(device);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(location).build(); // 201 + Location header
    }

    @Operation(summary = "Retrieves a device by ID",
               description = "Fetches the details of a device specified by its ID.")
    @ApiResponse(responseCode = "200", description = "Device retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Device not found")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<DeviceDetailsDTO> getDevice(@Parameter(description= "The UUID of the device to retrieve") 
    @PathVariable UUID id) {
        return ResponseEntity.ok(deviceService.findDeviceById(id));
    }

    @Operation(summary = "Deletes a device by ID",
               description = "Deletes the device specified by its ID from the system.")
    @ApiResponse(responseCode = "204", description = "Device deleted successfully")
    @ApiResponse(responseCode = "404", description = "Device not found")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteDevice(@Parameter(description= "The UUID of the device to delete")
    @PathVariable UUID id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
    @Operation(summary = "Assigns a user to a device",
               description = "Assigns a user to a device specified by their IDs.")
    @ApiResponse(responseCode = "200", description = "User assigned to device successfully")
    @ApiResponse(responseCode = "404", description = "Device or User not found")
    @PostMapping("/{deviceId}/users/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> assignUserToDevice(
            @PathVariable UUID deviceId,
            @PathVariable UUID userId) {
        deviceUserService.assignDeviceToUser(deviceId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Get all devices for the currently authenticated user (Client role).
     */
    @Operation(summary = "Retrieves devices for the authenticated user",
               description = "Fetches a list of devices assigned to the currently authenticated user.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved user's devices")
    @ApiResponse(responseCode = "401", description = "Unauthorized access")
    @GetMapping("/my-devices")
    @PreAuthorize("hasRole('ROLE_USER')") // Redundant check, but good practice
    public ResponseEntity<List<DeviceDTO>> getMyDevices() {
        return ResponseEntity.ok(deviceService.findDevicesByUsername());
    }

    /**
     * Update an existing device (Admin role).
     */
    @Operation(summary = "Updates an existing device",
               description = "Updates the details of a device specified by its ID.")
    @ApiResponse(responseCode = "200", description = "Device updated successfully")
    @ApiResponse(responseCode = "404", description = "Device not found")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<DeviceDetailsDTO> updateDevice(
            @PathVariable UUID id, 
            @Valid @RequestBody DeviceDetailsDTO deviceDetails) {
        
        DeviceDetailsDTO updatedDevice = deviceService.update(id, deviceDetails);
        return ResponseEntity.ok(updatedDevice);
    }
}

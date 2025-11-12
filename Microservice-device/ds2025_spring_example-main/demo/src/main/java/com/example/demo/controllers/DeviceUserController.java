package com.example.demo.controllers;

import com.example.demo.services.DeviceUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/device-users")
public class DeviceUserController {

    private final DeviceUserService deviceUserService;

    public DeviceUserController(DeviceUserService deviceUserService) {
        this.deviceUserService = deviceUserService;
    }

    /**
     * Assigns a user to a device.
     * Example: POST /device-users/assign?deviceId=...&userId=...
     */
    @Operation(summary = "Assigns a user to a device",
               description = "Assigns a user to a device specified by their IDs.")
    @ApiResponse(responseCode = "200", description = "User assigned to device successfully")
    @ApiResponse(responseCode = "404", description = "Device or User not found")
    @PostMapping("/assign")
    public ResponseEntity<String> assignUserToDevice(
            @RequestParam UUID deviceId,
            @RequestParam UUID userId) {

        deviceUserService.assignDeviceToUser(deviceId, userId);
        return ResponseEntity.ok("User assigned to device successfully.");
    }

    /**
     * Removes a user from a device.
     * Example: DELETE /device-users/remove?deviceId=...&userId=...
     */
    @Operation(summary = "Removes a user from a device",
               description = "Removes the association of a user from a device specified by their IDs.")
    @ApiResponse(responseCode = "200", description = "User removed from device successfully") 
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeUserFromDevice(
            @RequestParam UUID deviceId,
            @RequestParam UUID userId) {

        deviceUserService.removeUserFromDevice(deviceId, userId);
        return ResponseEntity.ok("User removed from device successfully.");
    }

    /**
     * Lists all users assigned to a specific device.
     * Example: GET /device-users/by-device/{deviceId}
     */
    @Operation(summary = "Lists all users assigned to a specific device",
               description = "Fetches a list of user IDs assigned to the specified device.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users")
    @GetMapping("/by-device/{deviceId}")
    public ResponseEntity<List<UUID>> getUsersByDevice(@PathVariable UUID deviceId) {
        List<UUID> userIds = deviceUserService.getUsersByDevice(deviceId);
        return ResponseEntity.ok(userIds);
    }

    /**
     * Lists all devices assigned to a specific user.
     * Example: GET /device-users/by-user/{userId}
     */
    @Operation(summary = "Lists all devices assigned to a specific user",
               description = "Fetches a list of device IDs assigned to the specified user.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of devices")
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<UUID>> getDevicesByUser(@PathVariable UUID userId) {
        List<UUID> deviceIds = deviceUserService.getDevicesByUser(userId);
        return ResponseEntity.ok(deviceIds);
    }
}

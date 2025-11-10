package com.example.demo.controllers;

import com.example.demo.services.DeviceUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/by-device/{deviceId}")
    public ResponseEntity<List<UUID>> getUsersByDevice(@PathVariable UUID deviceId) {
        List<UUID> userIds = deviceUserService.getUsersByDevice(deviceId);
        return ResponseEntity.ok(userIds);
    }

    /**
     * Lists all devices assigned to a specific user.
     * Example: GET /device-users/by-user/{userId}
     */
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<UUID>> getDevicesByUser(@PathVariable UUID userId) {
        List<UUID> deviceIds = deviceUserService.getDevicesByUser(userId);
        return ResponseEntity.ok(deviceIds);
    }
}

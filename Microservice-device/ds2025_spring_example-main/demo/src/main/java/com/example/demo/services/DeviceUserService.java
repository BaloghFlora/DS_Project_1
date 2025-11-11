package com.example.demo.services;

import com.example.demo.entities.Device;
import com.example.demo.entities.DeviceUser;
import com.example.demo.entities.User;
import com.example.demo.repositories.DeviceRepository;
import com.example.demo.repositories.DeviceUserRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.stereotype.Service;
// [!code ++]
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
// [!code --]

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeviceUserService {

    private final DeviceUserRepository deviceUserRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    public DeviceUserService(DeviceUserRepository deviceUserRepository,
                             DeviceRepository deviceRepository,
                             UserRepository userRepository) {
        this.deviceUserRepository = deviceUserRepository;
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
    }

    public void assignDeviceToUser(UUID deviceId, UUID userId) {
        // [!code ++]
        // --- CHECK FOR EXISTING ASSIGNMENT ---
        if (deviceUserRepository.existsByDeviceIdAndUserId(deviceId, userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already assigned to this device");
        }
        // [!code --]

        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        DeviceUser link = new DeviceUser(device, user);
        deviceUserRepository.save(link);
    }

    public void removeUserFromDevice(UUID deviceId, UUID userId) {
        deviceUserRepository.deleteByDeviceIdAndUserId(deviceId, userId);
    }

    public List<UUID> getUsersByDevice(UUID deviceId) {
        return deviceUserRepository.findByDeviceId(deviceId)
                .stream()
                .map(deviceUser -> deviceUser.getUser().getId())
                .collect(Collectors.toList());
    }

    public List<UUID> getDevicesByUser(UUID userId) {
        return deviceUserRepository.findByUserId(userId)
                .stream()
                .map(deviceUser -> deviceUser.getDevice().getId())
                .collect(Collectors.toList());
    }
}
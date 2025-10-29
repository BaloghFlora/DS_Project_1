package com.example.demo.dtos.builders;

import com.example.demo.dtos.DeviceDTO;
import com.example.demo.dtos.DeviceDetailsDTO;
import com.example.demo.entities.Device;

public class DeviceBuilder {

    private DeviceBuilder() {}

    public static DeviceDTO toDeviceDTO(Device device) {
        return new DeviceDTO(
                device.getId(),
                device.getDeviceName(),
                device.getDeviceUserId(),
                device.getDeviceStatus()
        );
    }

    public static DeviceDetailsDTO toDeviceDetailsDTO(Device device) {
        return new DeviceDetailsDTO(
                device.getId(),
                device.getDeviceName(),
                device.getDeviceUserId(),
                device.getDeviceStatus()
        );
    }

    public static Device toEntity(DeviceDetailsDTO dto) {
        return new Device(
                dto.getDeviceName(),
                dto.getDeviceUserId(),
                dto.getDeviceStatus()
        );
    }
}

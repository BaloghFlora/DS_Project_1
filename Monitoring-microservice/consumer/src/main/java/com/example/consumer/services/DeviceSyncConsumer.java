package com.example.consumer.services;

import com.example.consumer.config.RabbitConfig;
import com.example.consumer.entities.Device;
import com.example.consumer.repositories.DeviceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.dtos.SynchronizationEventDTO; // Placeholder - ensure the DTO is in your common/local DTO package

@Service
public class DeviceSyncConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceSyncConsumer.class);

    private final DeviceRepository deviceRepository;
    private final ObjectMapper objectMapper;

    public DeviceSyncConsumer(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
        this.objectMapper = new ObjectMapper();
    }

    @RabbitListener(queues = RabbitConfig.DEVICE_SYNC_QUEUE)
    @Transactional
    public void receiveDeviceSyncMessage(String jsonMessage) {
        SynchronizationEventDTO event;
        try {
            // NOTE: Replace com.example.demo.dtos.SynchronizationEventDTO with the correct path 
            // if you put the DTO in a different package.
            event = objectMapper.readValue(jsonMessage, SynchronizationEventDTO.class);
            LOGGER.debug("Received Device Sync Event: {}", event.toString());
        } catch (Exception e) {
            LOGGER.error("Failed to deserialize sync message: {}", jsonMessage, e);
            return;
        }

        if (!"DEVICE".equals(event.getEntityType())) {
            return; 
        }

        try {
            switch (event.getAction()) {
                case "CREATED":
                case "UPDATED":
                    // Use the incoming ID and deviceName to sync the local table.
                    Device deviceToSync = new Device(event.getId(), event.getName());
                    deviceRepository.save(deviceToSync);
                    LOGGER.info("Synced/Updated Device in monitoring-service: ID {} | Name {}", event.getId(), event.getName());
                    break;
                case "DELETED":
                    deviceRepository.deleteById(event.getId());
                    LOGGER.info("Deleted Device from monitoring-service with ID: {}", event.getId());
                    break;
                default:
                    LOGGER.warn("Unknown action received for DEVICE entity: {}", event.getAction());
            }
        } catch (Exception e) {
            LOGGER.error("Failed to process sync event for device ID {}: {}", event.getId(), e.getMessage());
            throw new RuntimeException("Error during device synchronization processing.", e);
        }
    }
}
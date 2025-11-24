package com.example.demo.services;

import com.example.demo.config.RabbitConfig;
import com.example.demo.dtos.SynchronizationEventDTO;
import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserSyncConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserSyncConsumer.class);

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    /**
     * The ObjectMapper and UserRepository are injected by Spring.
     * @param userRepository Repository for the local 'users' table.
     */
    public UserSyncConsumer(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Listens to the dedicated queue for user synchronization events.
     * The @Transactional annotation ensures that the entire operation either succeeds or fails.
     */
    @RabbitListener(queues = RabbitConfig.USER_SYNC_QUEUE)
    @Transactional
    public void receiveUserSyncMessage(String jsonMessage) {
        SynchronizationEventDTO event;
        try {
            event = objectMapper.readValue(jsonMessage, SynchronizationEventDTO.class);
            LOGGER.debug("Received User Sync Event: {}", event.toString());
        } catch (Exception e) {
            LOGGER.error("Failed to deserialize sync message: {}", jsonMessage, e);
            return;
        }

        // Only process events intended for synchronization (i.e., from the User service)
        if (!"USER".equals(event.getEntityType())) {
            return; 
        }

        try {
            switch (event.getAction()) {
                case "CREATED":
                case "UPDATED":
                    User userToSync = userRepository.findById(event.getId())
                    .orElse(new User());
    
                    userToSync.setId(event.getId());
                    userToSync.setUsername(event.getEmail()); // Update details
    
                    userRepository.save(userToSync);
                    LOGGER.info("Synced/Updated User...");
                    break;
                case "DELETED":
                    userRepository.deleteById(event.getId());
                    LOGGER.info("Deleted User from device-service with ID: {}", event.getId());
                    break;
                default:
                    LOGGER.warn("Unknown action received for USER entity: {}", event.getAction());
            }
        } catch (Exception e) {
            // Log the error. The @Transactional annotation will handle rollback.
            // Note: RabbitMQ will typically retry on any exception thrown here.
            LOGGER.error("Failed to process sync event for user ID {}: {}", event.getId(), e.getMessage());
            throw new RuntimeException("Error during user synchronization processing.", e);
        }
    }
}
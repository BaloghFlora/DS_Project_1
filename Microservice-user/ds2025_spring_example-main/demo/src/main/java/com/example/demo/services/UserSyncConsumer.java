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

    public UserSyncConsumer(UserRepository userRepository) {
        this.userRepository = userRepository;
        // Using a fresh ObjectMapper for thread safety if it were complex, but good practice.
        this.objectMapper = new ObjectMapper(); 
    }

    /**
     * Listens to the dedicated queue for user synchronization events.
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

        if (!"USER".equals(event.getEntityType())) {
            return; 
        }

        try {
            switch (event.getAction()) {
                case "CREATED":
                case "UPDATED":
                    // User entity in device-service only stores ID and username (which is the email)
                    User userToSync = new User(event.getEmail());
                    userToSync.setId(event.getId()); 
                    
                    userRepository.save(userToSync); // INSERT or UPDATE based on ID
                    LOGGER.info("Synced/Updated User in device-service: ID {} | Email {}", event.getId(), event.getEmail());
                    break;
                case "DELETED":
                    userRepository.deleteById(event.getId());
                    LOGGER.info("Deleted User from device-service with ID: {}", event.getId());
                    break;
                default:
                    LOGGER.warn("Unknown action received for USER entity: {}", event.getAction());
            }
        } catch (Exception e) {
            LOGGER.error("Failed to process sync event for user ID {}: {}", event.getId(), e.getMessage());
            // Throwing a RuntimeException here will cause Spring AMQP to reject and potentially retry the message
            throw new RuntimeException("Error during user synchronization processing.", e);
        }
    }
}
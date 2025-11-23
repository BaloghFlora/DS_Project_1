package com.example.demo.services;

import com.example.demo.config.RabbitConfig;
import com.example.demo.dtos.SynchronizationEventDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class SynchronizationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SynchronizationService.class);
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public SynchronizationService(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishEvent(SynchronizationEventDTO event) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(event);
            // Send to Fanout Exchange
            rabbitTemplate.convertAndSend(RabbitConfig.SYNC_EXCHANGE, "", jsonMessage);
            LOGGER.info("Published sync event to exchange {}: {} {}", RabbitConfig.SYNC_EXCHANGE, event.getEntityType(), event.getAction());
        } catch (Exception e) {
            LOGGER.error("Failed to publish sync event for {}: {}", event.getId(), e.getMessage(), e);
        }
    }
}
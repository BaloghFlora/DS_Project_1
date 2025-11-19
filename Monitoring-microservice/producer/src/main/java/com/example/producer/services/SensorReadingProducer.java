package com.example.producer.services;

import com.example.producer.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SensorReadingProducer {

    private final RabbitTemplate rabbitTemplate;
    private final Random random = new Random();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SensorReadingProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedRate = 5000)
    public void sendRandomData() {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            data.put("measurement", 20 + random.nextDouble() * 10);
            data.put("sensorId", "6d5be097-fbbc-4a2c-a055-2973e2580134");

            String jsonString = objectMapper.writeValueAsString(data);
            rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_NAME, jsonString);
            System.out.println("sent message:" + data.get("timestamp") + " | " + data.get("measurement"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

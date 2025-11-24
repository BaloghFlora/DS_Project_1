package com.example.producer.services;

import com.example.producer.config.RabbitConfig;
import com.example.producer.entities.Device;
import com.example.producer.repositories.DeviceRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SensorReadingProducer {

    private final RabbitTemplate rabbitTemplate;
    private final DeviceRepository deviceRepository; // Inject Repository
    private final Random random = new Random();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private final List<UUID> configDeviceIds = new ArrayList<>(); // IDs from file

    private LocalDateTime currentTimestamp = LocalDateTime.now();

    public SensorReadingProducer(RabbitTemplate rabbitTemplate, DeviceRepository deviceRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.deviceRepository = deviceRepository;
        this.loadConfigFileIds(); 
    }

    private void loadConfigFileIds() {
        try {
            ClassPathResource resource = new ClassPathResource("device_config.json");
            if (resource.exists()) {
                InputStream inputStream = resource.getInputStream();
                JsonNode rootNode = objectMapper.readTree(inputStream);
                JsonNode idsNode = rootNode.get("deviceIds");
                if (idsNode.isArray()) {
                    for (JsonNode idNode : idsNode) {
                        this.configDeviceIds.add(UUID.fromString(idNode.asText()));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Config file load error (ignoring): " + e.getMessage());
        }
    }

    @Scheduled(fixedRate = 5000)
    public void sendRandomData() {
        // 1. Fetch IDs from Database
        List<UUID> dbDeviceIds = deviceRepository.findAll().stream()
                .map(Device::getId)
                .collect(Collectors.toList());

        // 2. Merge with Config IDs (Set handles duplicates)
        Set<UUID> allDeviceIds = new HashSet<>(configDeviceIds);
        allDeviceIds.addAll(dbDeviceIds);

        if (allDeviceIds.isEmpty()) {
            System.out.println("No devices found in DB or Config. Skipping.");
            return;
        }

        // 3. Update Time
        currentTimestamp = currentTimestamp.plusMinutes(10);
        String formattedTime = currentTimestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        // 4. Calculate Measurement
        int hour = currentTimestamp.getHour();
        double baseLoad = (hour >= 6 && hour < 18) ? 15.0 : (hour >= 18 ? 25.0 : 5.0);

        // 5. Send for ALL devices
        try {
            for (UUID deviceId : allDeviceIds) {
                double measurement = baseLoad + random.nextDouble() * 5;

                Map<String, Object> data = new HashMap<>();
                data.put("timestamp", formattedTime);
                data.put("measurement", measurement);
                data.put("sensorId", deviceId.toString());

                String jsonString = objectMapper.writeValueAsString(data);
                rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_NAME, jsonString);
            }
            System.out.println("Sent readings for " + allDeviceIds.size() + " devices at " + formattedTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
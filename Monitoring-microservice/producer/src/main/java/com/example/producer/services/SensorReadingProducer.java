package com.example.producer.services;

import com.example.producer.config.RabbitConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class SensorReadingProducer {

    private final RabbitTemplate rabbitTemplate;
    private final RestTemplate restTemplate;
    private final Random random = new Random();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Assuming these services are reachable via these hostnames in Docker
    // You might need to adjust application.properties or docker-compose networks
    @Value("${auth.service.url:http://auth-service:8081}")
    private String authServiceUrl;

    @Value("${device.service.url:http://device_api_service:8080}")
    private String deviceServiceUrl;

    // Start simulation time at "now"
    private LocalDateTime currentTimestamp = LocalDateTime.now();

    public SensorReadingProducer(RabbitTemplate rabbitTemplate, RestTemplate restTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedRate = 5000) // Runs every 5 seconds (wall-clock time)
    public void sendRandomData() {
        try {
            // 1. Increment simulation time by 10 minutes
            currentTimestamp = currentTimestamp.plusMinutes(10);
            String formattedTime = currentTimestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // 2. Authenticate to get Token (Login as Admin)
            String token = getJwtToken();
            if (token == null) {
                System.err.println("Failed to obtain JWT token. Skipping cycle.");
                return;
            }

            // 3. Fetch Device IDs from Backend
            List<UUID> deviceIds = fetchDeviceIds(token);
            if (deviceIds.isEmpty()) {
                System.out.println("No devices found. Skipping cycle.");
                return;
            }

            // 4. Iterate and send message for each device
            for (UUID deviceId : deviceIds) {
                Map<String, Object> data = new HashMap<>();
                data.put("timestamp", formattedTime);
                data.put("measurement", 20 + random.nextDouble() * 10);
                data.put("sensorId", deviceId.toString()); // Use actual Device ID

                String jsonString = objectMapper.writeValueAsString(data);
                rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_NAME, jsonString);
                
                System.out.println("Sent for device " + deviceId + ": " + formattedTime + " | " + data.get("measurement"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getJwtToken() {
        try {
            String url = authServiceUrl + "/auth/login";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("username", "admin");    // Using pre-loaded admin credentials
            map.add("password", "adminpass");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return (String) response.getBody().get("token");
            }
        } catch (Exception e) {
            System.err.println("Error logging in: " + e.getMessage());
        }
        return null;
    }

private List<UUID> fetchDeviceIds(String token) {
        try {
            String url = deviceServiceUrl + "/devices"; 
            HttpHeaders headers = new HttpHeaders();
            
            // Standard Authorization header
            headers.set("Authorization", "Bearer " + token);
            
            // [!code ++]
            // --- FIX: Manually inject Gateway headers for internal communication ---
            // Since we bypass Traefik for this internal call, we must supply 
            // the headers that the Device Service's HeaderAuthBridgeFilter expects.
            headers.set("X-User", "admin");
            headers.set("X-Role", "ROLE_ADMIN");
            // [!code --]

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<UUID> ids = new ArrayList<>();
                for (Map<String, Object> deviceData : response.getBody()) {
                    String idStr = (String) deviceData.get("id");
                    ids.add(UUID.fromString(idStr));
                }
                return ids;
            }
        } catch (Exception e) {
            System.err.println("Error fetching devices: " + e.getMessage());
        }
        return Collections.emptyList();
    }
}
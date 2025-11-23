package com.example.consumer.services;

import com.example.consumer.entities.HourlyConsumption;
import com.example.consumer.repositories.HourlyConsumptionRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SensorConsumer {

    // --- Inject the new Hourly Consumption Repository ---
    private final HourlyConsumptionRepository hourlyRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Map to hold transient readings for aggregation: Key=DeviceId, Value=HourlyAccumulator
    // HourlyAccumulator stores: {hourTimestamp: LocalDateTime, totalConsumption: Double, count: Integer}
    private final ConcurrentHashMap<String, Map<String, Object>> hourlyAggregates = new ConcurrentHashMap<>();

    public SensorConsumer(HourlyConsumptionRepository hourlyRepository) {
        // Removed SensorReadingRepository, injected new HourlyConsumptionRepository
        this.hourlyRepository = hourlyRepository;
    }

    @RabbitListener(queues = "${app.queue.name}")
    public void receiveMessage(String jsonMessage) {
        try {
            Map<String, Object> message = objectMapper.readValue(jsonMessage, Map.class);

            String timestampStr = (String) message.get("timestamp");
            LocalDateTime rawTimestamp = LocalDateTime.parse(timestampStr, formatter);
            String deviceId = (String) message.get("sensorId");
            Double measurement = Double.parseDouble(message.get("measurement").toString());
            
            // Normalize timestamp to the start of the current hour (resetting minutes/seconds)
            LocalDateTime hourTimestamp = rawTimestamp.withMinute(0).withSecond(0).withNano(0);
            
            // Use computeIfAbsent to handle concurrent updates and ensure atomic logic per device
            hourlyAggregates.compute(deviceId, (key, currentAgg) -> {
                if (currentAgg == null) {
                    currentAgg = new ConcurrentHashMap<>();
                    currentAgg.put("hourTimestamp", hourTimestamp);
                    currentAgg.put("totalConsumption", 0.0);
                    currentAgg.put("count", 0);
                }

                LocalDateTime existingHour = (LocalDateTime) currentAgg.get("hourTimestamp");

                if (!existingHour.equals(hourTimestamp)) {
                    // Hour changed. Flush the previous hour's data first (if any was collected).
                    if ((Integer) currentAgg.get("count") > 0) {
                        flushAggregate(key, existingHour, (Double) currentAgg.get("totalConsumption"));
                    }
                    
                    // Start new aggregation for the current hour
                    currentAgg.put("hourTimestamp", hourTimestamp);
                    currentAgg.put("totalConsumption", measurement);
                    currentAgg.put("count", 1);
                    
                } else {
                    // Same hour, aggregate the data.
                    Double newTotal = (Double) currentAgg.get("totalConsumption") + measurement;
                    Integer newCount = (Integer) currentAgg.get("count") + 1;
                    
                    currentAgg.put("totalConsumption", newTotal);
                    currentAgg.put("count", newCount);

                    // Since readings are every 10 minutes, 6 readings should complete an hour.
                    if (newCount >= 6) {
                        flushAggregate(key, hourTimestamp, newTotal);
                        // Remove entry after successfully flushing the full hour data
                        return null; 
                    }
                }
                
                System.out.println("Processing reading for device " + deviceId + " at " + timestampStr + ". Count: " + currentAgg.get("count"));
                return currentAgg;
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Separate method to save data to the database
    private void flushAggregate(String deviceId, LocalDateTime hourTimestamp, Double totalConsumption) {
        HourlyConsumption hourly = new HourlyConsumption(deviceId, hourTimestamp, totalConsumption);
        hourlyRepository.save(hourly);
        System.out.println("FLUSHED HOURLY AGGREGATE for " + deviceId + " at " + hourTimestamp + " with total: " + totalConsumption + " KWh.");
    }
}
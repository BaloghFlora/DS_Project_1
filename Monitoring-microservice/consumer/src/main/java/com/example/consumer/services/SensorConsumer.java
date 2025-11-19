package com.example.consumer.services;

import com.example.consumer.entities.SensorReading;
import com.example.consumer.repositories.SensorReadingRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
public class SensorConsumer {

    private final SensorReadingRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SensorConsumer(SensorReadingRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = "${app.queue.name}")
    public void receiveMessage(String jsonMessage) {
        try {
            Map<String, Object> message = objectMapper.readValue(jsonMessage, Map.class);

            SensorReading reading = new SensorReading();
            reading.setTimestamp((String) message.get("timestamp"));
            reading.setMeasurement(Double.parseDouble(message.get("measurement").toString()));
            reading.setSensorId((String) message.get("sensorId"));

            repository.save(reading);
            System.out.println("inserted into db: " + reading.getTimestamp() + " | " + reading.getMeasurement());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

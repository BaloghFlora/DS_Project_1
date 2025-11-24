package com.example.consumer.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // Existing Sensor Data Queue
    public static final String QUEUE_NAME = "sensor.data.queue";

    // --- NEW: Device Synchronization Configuration ---
    public static final String SYNC_EXCHANGE = "sync.exchange";
    public static final String DEVICE_SYNC_QUEUE = "device.sync.monitoring.queue"; // Unique name for this service's queue

    @Bean
    public Queue sensorDataQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    // Define the Sync Exchange (it must match the one defined in Producer services)
    @Bean
    public FanoutExchange syncExchange() {
        return new FanoutExchange(SYNC_EXCHANGE, true, false);
    }

    // Define the Queue specific for Monitoring Service to receive Device events
    @Bean
    public Queue deviceSyncQueue() {
        return new Queue(DEVICE_SYNC_QUEUE, true);
    }

    // Bind the Queue to the Exchange
    @Bean
    public Binding deviceSyncBinding(Queue deviceSyncQueue, FanoutExchange syncExchange) {
        return BindingBuilder.bind(deviceSyncQueue).to(syncExchange);
    }
}
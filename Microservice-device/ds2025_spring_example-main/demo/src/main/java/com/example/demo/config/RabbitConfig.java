package com.example.demo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit; // <-- Ensure this is enabled in main Application class

@Configuration
@EnableRabbit
public class RabbitConfig {
    
    public static final String SYNC_EXCHANGE = "sync.exchange"; 
    // Dedicated queue for Device Service to receive user sync events
    public static final String USER_SYNC_QUEUE = "user.sync.device.queue"; 
    
    @Bean
    public FanoutExchange syncExchange() {
        // Durable, not auto-delete
        return new FanoutExchange(SYNC_EXCHANGE, true, false); 
    }

    @Bean
    public Queue userSyncDeviceQueue() {
        return new Queue(USER_SYNC_QUEUE, true); // Durable
    }
    
    @Bean
    public Binding userSyncBinding(Queue userSyncDeviceQueue, FanoutExchange syncExchange) {
        // Binds the dedicated queue to the fanout exchange
        return BindingBuilder.bind(userSyncDeviceQueue).to(syncExchange);
    }
}
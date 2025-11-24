package com.example.demo.config;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String SYNC_EXCHANGE = "sync.exchange";
    
    // Only the exchange is needed for the producer. 
    // Queues are defined in the consumer services (Device/Monitoring).
    @Bean
    public FanoutExchange syncExchange() {
        return new FanoutExchange(SYNC_EXCHANGE, true, false);
    }
}
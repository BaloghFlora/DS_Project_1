package com.example.demo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String SYNC_EXCHANGE = "sync.exchange";
    public static final String AUTH_SYNC_QUEUE = "user.sync.auth.queue";

    @Bean
    public FanoutExchange syncExchange() {
        return new FanoutExchange(SYNC_EXCHANGE, true, false);
    }

    @Bean
    public Queue authSyncQueue() {
        return new Queue(AUTH_SYNC_QUEUE, true);
    }

    @Bean
    public Binding authSyncBinding(Queue authSyncQueue, FanoutExchange syncExchange) {
        return BindingBuilder.bind(authSyncQueue).to(syncExchange);
    }
}
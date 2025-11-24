package com.example.demo.services;

import com.example.demo.config.RabbitConfig;
import com.example.demo.dtos.SynchronizationEventDTO; // Ensure you created this DTO class
import com.example.demo.entities.Credential;
import com.example.demo.repositories.CredentialRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthSyncConsumer {

    private final CredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    public AuthSyncConsumer(CredentialRepository credentialRepository, PasswordEncoder passwordEncoder) {
        this.credentialRepository = credentialRepository;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = new ObjectMapper();
    }

    @RabbitListener(queues = RabbitConfig.AUTH_SYNC_QUEUE)
    @Transactional
    public void receiveSyncMessage(String jsonMessage) {
        try {
            SynchronizationEventDTO event = objectMapper.readValue(jsonMessage, SynchronizationEventDTO.class);

            if (!"USER".equals(event.getEntityType())) return;

            switch (event.getAction()) {
                case "CREATED":
                    if (credentialRepository.findByUsername(event.getEmail()).isEmpty()) {
                        Credential credential = new Credential(
                                event.getEmail(),
                                passwordEncoder.encode(event.getPassword()),
                                "ROLE_USER" // Default role
                        );
                        credentialRepository.save(credential);
                        System.out.println("Auth Sync: Created credential for " + event.getEmail());
                    }
                    break;

                case "UPDATED":
                    Optional<Credential> existing = credentialRepository.findByUsername(event.getEmail());
                    if (existing.isPresent()) {
                        Credential cred = existing.get();
                        // Update password if provided
                        if (event.getPassword() != null && !event.getPassword().isEmpty()) {
                            cred.setPassword(passwordEncoder.encode(event.getPassword()));
                        }
                        // If username/email changed in User Service, you might handle it here too
                        credentialRepository.save(cred);
                        System.out.println("Auth Sync: Updated credential for " + event.getEmail());
                    }
                    break;

                case "DELETED":
                    credentialRepository.findByUsername(event.getEmail())
                            .ifPresent(credentialRepository::delete);
                    System.out.println("Auth Sync: Deleted credential for " + event.getEmail());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
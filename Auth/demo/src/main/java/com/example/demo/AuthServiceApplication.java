package com.example.demo;

import com.example.demo.entities.Credential;
import com.example.demo.repositories.CredentialRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}
	
	@Bean // <-- You already have this
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean // <-- Add this whole method
    CommandLineRunner initDatabase(CredentialRepository repository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Only add users if the admin user doesn't exist
            if (repository.findByUsername("admin").isEmpty()) {
                System.out.println("Pre-loading admin user...");
                Credential admin = new Credential(
                        "admin",
                        passwordEncoder.encode("adminpass"), // Correctly hash the password
                        "ROLE_ADMIN,ROLE_USER"
                );
                repository.save(admin);
                System.out.println("Admin user pre-loaded.");
            }
            
            if (repository.findByUsername("user").isEmpty()) {
                System.out.println("Pre-loading regular user...");
                Credential user = new Credential(
                        "user",
                        passwordEncoder.encode("userpass"), // Correctly hash the password
                        "ROLE_USER"
                );
                repository.save(user);
                System.out.println("Regular user pre-loaded.");
            }
        };
    }
}
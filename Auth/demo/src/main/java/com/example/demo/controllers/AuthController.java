package com.example.demo.controllers;

import com.example.demo.entities.Credential;
import com.example.demo.repositories.CredentialRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final String SECRET = "supersecret123456supersecret123456";
    private final CredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;

    private final String USER_SERVICE_URL = "http://user-service:8080/people";

    public AuthController(CredentialRepository credentialRepository,
                          PasswordEncoder passwordEncoder,
                          RestTemplate restTemplate) {
        this.credentialRepository = credentialRepository;
        this.passwordEncoder = passwordEncoder;
        this.restTemplate = restTemplate;
    }

    // This DTO must match the PersonDetailsDTO from your user-service
    // --- FIX: ADDED GETTERS AND SETTERS ---
    public static class PersonDetailsDTO {
        private String fullName;
        private String email;
        private String password;

        // Getters
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getPassword() { return password; }

        // Setters
        public void setFullName(String fullName) { this.fullName = fullName; }
        public void setEmail(String email) { this.email = email; }
        public void setPassword(String password) { this.password = password; }
    }

    // This DTO is for the register request body
    public static class RegisterRequest {
        public String username;
        public String email;
        public String password;
        public String role; // "USER" or "ADMIN"
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        if (credentialRepository.findByUsername(request.username).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken");
        }

        String role = "ROLE_USER";
        if (request.role != null && request.role.equalsIgnoreCase("ADMIN")) {
            role = "ROLE_ADMIN";
        }
        
        String hashedPassword = passwordEncoder.encode(request.password);

        // 1. Create and save the Credential
        Credential credential = new Credential(
                request.username,
                hashedPassword,
                role
        );
        credentialRepository.save(credential);

        // 2. Prepare the request for the user-service
        PersonDetailsDTO personDTO = new PersonDetailsDTO();
        personDTO.setFullName(request.username); // --- FIX: Use setter
        personDTO.setEmail(request.email);       // --- FIX: Use setter
        personDTO.setPassword(hashedPassword);   // --- FIX: Use setter
        
        try {
            // 3. Call the user-service to create the Person profile
            ResponseEntity<String> response = restTemplate.postForEntity(
                    USER_SERVICE_URL,
                    personDTO,
                    String.class
            );

            if (response.getStatusCode() != HttpStatus.CREATED) {
                throw new RuntimeException("Failed to create user profile. Status: " + response.getStatusCode());
            }

        } catch (Exception e) {
            // Rollback: delete the credential if the profile creation fails
            credentialRepository.delete(credential);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create user profile: " + e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestParam String username, @RequestParam String password) {
        
        Credential credential = credentialRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(password, credential.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        var key = Keys.hmacShaKeyFor(SECRET.getBytes());
        
        List<String> roles = List.of(credential.getRole());
        if (credential.getRole().equals("ROLE_ADMIN")) {
            roles = List.of("ROLE_ADMIN", "ROLE_USER");
        }

        String token = Jwts.builder()
                .subject(credential.getUsername())
                .claim("role", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600_000))
                .signWith(key)
                .compact();

        return Map.of("token", token, "user", credential.getUsername(), "roles", String.join(",", roles));
    }
}
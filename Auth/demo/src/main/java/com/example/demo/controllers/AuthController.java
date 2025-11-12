package com.example.demo.controllers;

import com.example.demo.entities.Credential;
import com.example.demo.repositories.CredentialRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final String SECRET = "supersecret123456supersecret123456";

    private final CredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;

    // --- Constructor to inject the Repository and Encoder ---
    public AuthController(CredentialRepository credentialRepository, PasswordEncoder passwordEncoder) {
        this.credentialRepository = credentialRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Operation(summary = "User login",
               description = "Authenticates a user and returns a JWT token upon successful login.") 
    @ApiResponse(responseCode = "200", description = "Login successful")
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam String username, @RequestParam String password) {

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

        Map<String, String> response = Map.of(
                "token", token,
                "user", credential.getUsername(),
                "roles", String.join(",", roles));

        return ResponseEntity.ok(response); // Return ResponseEntity with OK status
    }
    @Operation(summary = "User registration",
               description = "Registers a new user with the provided username, password, and role.")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestParam String username, @RequestParam String password,
            @RequestParam String role) {
        if (credentialRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // User already exists
        }

        Credential credential = new Credential(
                username,
                passwordEncoder.encode(password),
                role);
        credentialRepository.save(credential);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @Operation(summary = "Delete user",
               description = "Deletes an existing user specified by their username.")
    @ApiResponse(responseCode = "200", description = "User deleted successfully")
    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam String username) {
        Credential credential = credentialRepository.findByUsername(username)
                .orElse(null);
        if (credential == null) {
            return ResponseEntity.notFound().build();
        }
        credentialRepository.delete(credential);
        return ResponseEntity.ok().build();
    }
}
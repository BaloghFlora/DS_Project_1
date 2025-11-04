package com.example.demo.controllers;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final String SECRET = "supersecret123456supersecret123456";

    // Hardcoded "database"
    private static final Map<String, String> USERS = Map.of(
            "admin", "adminpass",
            "user", "userpass"
    );

    private static final Map<String, List<String>> ROLES = Map.of(
            "admin", List.of("ROLE_ADMIN", "ROLE_USER"),
            "user", List.of("ROLE_USER")
    );

    @PostMapping("/login")
    public Map<String, String> login(@RequestParam String username, @RequestParam String password) {
        if (!USERS.containsKey(username) || !USERS.get(username).equals(password)) {
            throw new RuntimeException("Invalid username or password");
        }

        var key = Keys.hmacShaKeyFor(SECRET.getBytes());
        var roles = ROLES.get(username);

        String token = Jwts.builder()
                .subject(username)
                .claim("role", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600_000)) // 1 hour
                .signWith(key)
                .compact();

        return Map.of("token", token, "user", username, "roles", String.join(",", roles));
    }
}

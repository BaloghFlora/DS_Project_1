package com.example.demo.config; 

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // <-- ADD THIS IMPORT
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true) 
public class SecurityConfig {

    private final HeaderAuthBridgeFilter headerAuthBridgeFilter;

    public SecurityConfig(HeaderAuthBridgeFilter headerAuthBridgeFilter) {
        this.headerAuthBridgeFilter = headerAuthBridgeFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // --- THIS IS THE FIX ---
                        // Allow the POST /people endpoint to be called without authentication
                        // This is for the auth-service to create user profiles.
                        .requestMatchers(HttpMethod.POST, "/people").permitAll() 
                        // ---------------------
                        .anyRequest().authenticated() // All other requests still require auth
                )
                .exceptionHandling(ex -> ex.accessDeniedHandler((req, res, e) -> {
                    res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    res.getWriter().write("403 Forbidden: Insufficient role or invalid authentication");
                }))
                .addFilterBefore(headerAuthBridgeFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
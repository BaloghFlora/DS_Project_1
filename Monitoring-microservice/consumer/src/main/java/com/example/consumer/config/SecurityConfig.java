package com.example.consumer.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
            .csrf(csrf -> csrf.disable()) // Disable CSRF (we use JWT)
            .authorizeHttpRequests(auth -> auth
                // Allow Swagger UI if needed, otherwise secure everything
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated() 
            )
            // Return 403 Forbidden instead of 401 + Login Page on auth failure
            .exceptionHandling(ex -> ex.accessDeniedHandler((req, res, e) -> {
                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                res.getWriter().write("403 Forbidden: Insufficient role");
            }))
            // Add our custom filter before the standard authentication filter
            .addFilterBefore(headerAuthBridgeFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
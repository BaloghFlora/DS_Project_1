package com.example.user_service.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/users/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String userProfile(HttpServletRequest req) {
        String user = req.getHeader("X-User");
        String role = req.getHeader("X-Role");
        System.out.println("X-User = " + user);
        System.out.println("X-Role = " + role);
        return "Access granted to " + user + " with roles: " + role;
    }

    @GetMapping("/users/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminEndpoint(HttpServletRequest req) {
        String user = req.getHeader("X-User");
        String role = req.getHeader("X-Role");
        System.out.println("ADMIN ENDPOINT HIT by " + user + " [" + role + "]");
        return "Access granted: ADMIN endpoint for " + user;
    }
}

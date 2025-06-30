package com.parkmate.notificationservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "ParkMate Notification Service");
        return response;
    }

    @GetMapping("/rate-limit-test")
    public Map<String, Object> rateLimitTest() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Rate limit test successful");
        response.put("timestamp", LocalDateTime.now());
        response.put("remaining", "Check X-Rate-Limit-Remaining header");
        return response;
    }

    @GetMapping("/circuit-breaker-test")
    public Map<String, Object> circuitBreakerTest() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Circuit breaker test successful");
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
} 
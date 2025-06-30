package com.parkmate.notificationservice;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class
})
@RestController
@RequestMapping("/loadtest")
public class LoadTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoadTestApplication.class, args);
    }

    @Bean
    public Bucket rateLimitBucket() {
        Bandwidth limit = Bandwidth.classic(1000, Refill.greedy(1000, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "ParkMate Load Test Service");
        return response;
    }

    @GetMapping("/rate-limit")
    @RateLimiter(name = "api-rate-limiter")
    public Map<String, Object> rateLimitTest() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Rate limit test successful");
        response.put("timestamp", LocalDateTime.now());
        response.put("remaining", "Check X-Rate-Limit-Remaining header");
        return response;
    }

    @GetMapping("/circuit-breaker")
    @CircuitBreaker(name = "user-service")
    public Map<String, Object> circuitBreakerTest() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Circuit breaker test successful");
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    @GetMapping("/async")
    public CompletableFuture<Map<String, Object>> asyncTest() {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Async test successful");
            response.put("timestamp", LocalDateTime.now());
            response.put("thread", Thread.currentThread().getName());
            return response;
        });
    }

    @GetMapping("/metrics")
    public Map<String, Object> metrics() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("memory", Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        response.put("maxMemory", Runtime.getRuntime().maxMemory());
        response.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        return response;
    }
} 
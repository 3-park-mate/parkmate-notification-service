package com.parkmate.notificationservice.common.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitConfig {

    @Bean
    public Bucket apiRateLimitBucket() {
        // API 요청에 대한 Rate Limiting: 분당 1000개 요청
        Bandwidth limit = Bandwidth.classic(1000, Refill.greedy(1000, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    @Bean
    public Bucket fcmRateLimitBucket() {
        // FCM 전송에 대한 Rate Limiting: 분당 500개 전송
        Bandwidth limit = Bandwidth.classic(500, Refill.greedy(500, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    @Bean
    public Bucket userClientRateLimitBucket() {
        // 외부 사용자 서비스 호출에 대한 Rate Limiting: 분당 200개 요청
        Bandwidth limit = Bandwidth.classic(200, Refill.greedy(200, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
} 
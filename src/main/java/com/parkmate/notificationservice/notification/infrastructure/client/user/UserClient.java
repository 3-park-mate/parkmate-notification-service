package com.parkmate.notificationservice.notification.infrastructure.client.user;

import com.parkmate.notificationservice.common.response.ApiResponse;
import com.parkmate.notificationservice.notification.infrastructure.client.user.response.UserNameResponse;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserClient {

    private final WebClient webClient;
    private final CircuitBreakerFactory circuitBreakerFactory;
    private final Bucket userClientRateLimitBucket;

    private static final String BASE_URL = "http://USER-SERVICE";
    private static final String USER_UUID_HEADER = "X-User-UUID";

    public CompletableFuture<ApiResponse<UserNameResponse>> getUserName(String userUuid) {
        // Rate Limiting 체크
        ConsumptionProbe probe = userClientRateLimitBucket.tryConsumeAndReturnRemaining(1);
        if (!probe.isConsumed()) {
            log.warn("Rate limit exceeded for user service call: {}", userUuid);
            return CompletableFuture.completedFuture(null);
        }

        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("user-service");
        
        return circuitBreaker.run(
                () -> webClient.get()
                        .uri(BASE_URL + "/api/v1/users/name")
                        .header(USER_UUID_HEADER, userUuid)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<ApiResponse<UserNameResponse>>() {})
                        .toFuture(),
                throwable -> {
                    log.error("User service call failed for UUID: {}, error: {}", userUuid, throwable.getMessage());
                    return CompletableFuture.completedFuture(null);
                }
        );
    }
}

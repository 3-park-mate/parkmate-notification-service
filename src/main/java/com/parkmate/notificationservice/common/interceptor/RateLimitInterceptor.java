package com.parkmate.notificationservice.common.interceptor;

import com.parkmate.notificationservice.common.exception.BaseException;
import com.parkmate.notificationservice.common.exception.ResponseStatus;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Bucket apiRateLimitBucket;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        ConsumptionProbe probe = apiRateLimitBucket.tryConsumeAndReturnRemaining(1);
        
        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            return true;
        } else {
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
            log.warn("Rate limit exceeded for request: {} {}", request.getMethod(), request.getRequestURI());
            throw new BaseException(ResponseStatus.REQUEST_CONFLICT, "요청이 너무 많습니다. 잠시 후 다시 시도해주세요.");
        }
    }
} 
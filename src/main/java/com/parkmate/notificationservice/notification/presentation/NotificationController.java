package com.parkmate.notificationservice.notification.presentation;

import com.parkmate.notificationservice.notification.application.NotificationService;
import com.parkmate.notificationservice.notification.dto.response.NotificationResponseDto;
import com.parkmate.notificationservice.notification.vo.response.NotificationResponseVo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationResponseVo> getNotifications(@RequestParam String receiverUuid) {
        return notificationService.getNotificationsByReceiverUuid(receiverUuid)
                .stream()
                .map(NotificationResponseDto::toVo)
                .toList();
    }
}

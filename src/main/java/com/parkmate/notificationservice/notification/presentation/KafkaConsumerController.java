package com.parkmate.notificationservice.notification.presentation;

import com.parkmate.notificationservice.notification.application.NotificationService;
import com.parkmate.notificationservice.notification.dto.NotificationEventDto;
import com.parkmate.notificationservice.notification.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerController {

    private final NotificationService notificationService;

    @KafkaListener(topics = {"reservation.created"}, groupId = "notification-service", containerFactory = "notificationEventListener")
    public void consumeNotificationEvent(NotificationEvent notificationEvent) {
        log.info("Received message from reservation.complete topic: {}", notificationEvent);
        notificationService.createNotification(NotificationEventDto.from(notificationEvent));
    }
}

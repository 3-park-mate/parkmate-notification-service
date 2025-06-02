package com.parkmate.notificationservice.notification.application;

import com.parkmate.notificationservice.notification.dto.NotificationEventDto;
import com.parkmate.notificationservice.notification.dto.response.NotificationResponseDto;

import java.util.List;

public interface NotificationService {

    void createNotification(NotificationEventDto notificationEventDto);

    List<NotificationResponseDto> getNotificationsByReceiverUuid(String receiverUuid);
}

package com.parkmate.notificationservice.notification.event;

import com.parkmate.notificationservice.notification.domain.NotificationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
public class NotificationEvent {

    private String receiverUuid;
    private NotificationType notificationType;
    private String title;
    private String content;
}

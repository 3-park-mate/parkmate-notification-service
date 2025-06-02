package com.parkmate.notificationservice.notification.dto;

import com.parkmate.notificationservice.notification.domain.Notification;
import com.parkmate.notificationservice.notification.domain.NotificationType;
import com.parkmate.notificationservice.notification.event.NotificationEvent;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationEventDto {

    private String receiverUuid;
    private NotificationType notificationType;
    private String title;
    private String content;

    @Builder
    private NotificationEventDto(String receiverUuid,
                                NotificationType notificationType,
                                String title,
                                String content) {
        this.receiverUuid = receiverUuid;
        this.notificationType = notificationType;
        this.title = title;
        this.content = content;
    }

    public static NotificationEventDto from(NotificationEvent notificationEvent) {
        return NotificationEventDto.builder()
                .receiverUuid(notificationEvent.getReceiverUuid())
                .notificationType(notificationEvent.getNotificationType())
                .title(notificationEvent.getTitle())
                .content(notificationEvent.getContent())
                .build();
    }

    public Notification toEntity() {
        return Notification.builder()
                .receiverUuid(this.receiverUuid)
                .title(this.title)
                .content(this.content)
                .isRead(false)
                .build();
    }
}

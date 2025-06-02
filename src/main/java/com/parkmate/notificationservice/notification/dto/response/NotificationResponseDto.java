package com.parkmate.notificationservice.notification.dto.response;

import com.parkmate.notificationservice.notification.domain.Notification;
import com.parkmate.notificationservice.notification.vo.response.NotificationResponseVo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationResponseDto {

    private String title;
    private String content;
    private boolean isRead;

    @Builder
    private NotificationResponseDto(String title,
                                   String content,
                                   boolean isRead) {
        this.title = title;
        this.content = content;
        this.isRead = isRead;
    }

    public static NotificationResponseDto from(Notification notification) {
        return NotificationResponseDto.builder()
                .title(notification.getTitle())
                .content(notification.getContent())
                .isRead(notification.isRead())
                .build();
    }

    public NotificationResponseVo toVo() {
        return NotificationResponseVo.builder()
                .title(title)
                .content(content)
                .isRead(isRead)
                .build();
    }
}

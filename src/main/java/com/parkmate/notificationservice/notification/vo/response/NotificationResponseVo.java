package com.parkmate.notificationservice.notification.vo.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationResponseVo {

    private String title;
    private String content;
    private boolean isRead;

    @Builder
    private NotificationResponseVo(String title,
                                  String content,
                                  boolean isRead) {
        this.title = title;
        this.content = content;
        this.isRead = isRead;
    }
}

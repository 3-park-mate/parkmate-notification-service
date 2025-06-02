package com.parkmate.notificationservice.notification.domain;

import com.parkmate.notificationservice.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id
    private String id;
    private String receiverUuid;
    private String title;
    private String content;
    private boolean isRead;

    @Builder
    private Notification(String id,
                        String receiverUuid,
                        String title,
                        String content,
                        boolean isRead) {
        this.id = id;
        this.receiverUuid = receiverUuid;
        this.title = title;
        this.content = content;
        this.isRead = isRead;
    }
}

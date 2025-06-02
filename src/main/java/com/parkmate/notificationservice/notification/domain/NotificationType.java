package com.parkmate.notificationservice.notification.domain;

import lombok.Getter;

@Getter
public enum NotificationType {

    CHAT_MESSAGE("채팅 메시지"),
    EMPTY_SPOT_AVAILABLE("빈 주차 공간 알림"),
    RESERVATION_COMPLETED("주차 예약 완료"),
    PARKING_EXIT_REMINDER("주차장 퇴장 알림"),
    ;

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }
}

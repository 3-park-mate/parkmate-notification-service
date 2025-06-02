package com.parkmate.notificationservice.notification.application;

import com.parkmate.notificationservice.notification.infrastructure.NotificationReactiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationReactiveService {

    private final NotificationReactiveRepository notificationReactiveRepository;

    public void getNotifications() {

    }
}

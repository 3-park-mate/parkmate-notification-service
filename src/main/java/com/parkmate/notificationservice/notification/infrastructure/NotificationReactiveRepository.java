package com.parkmate.notificationservice.notification.infrastructure;

import com.parkmate.notificationservice.notification.domain.Notification;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface NotificationReactiveRepository extends ReactiveMongoRepository<Notification, String> {

}

package com.ritualfresh.notifications.realtime;

import com.ritualfresh.notifications.dto.NotificationItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificationRealtimeDispatcher {
    public static final String CREATED_EVENT = "notification.created";
    public static final String READ_EVENT = "notification.read";
    public static final String READ_ALL_EVENT = "notifications.read-all";

    private final NotificationRealtimePublisher publisher;

    public void notificationCreated(Long recipientId, NotificationItemResponse notification, long unreadCount) {
        afterCommit(() -> publisher.publish(
                recipientId,
                CREATED_EVENT,
                Map.of("notification", notification, "unreadCount", unreadCount)));
    }

    public void notificationRead(Long recipientId, Long notificationId, LocalDateTime readAt, long unreadCount) {
        afterCommit(() -> publisher.publish(
                recipientId,
                READ_EVENT,
                Map.of(
                        "notificationId", notificationId,
                        "readAt", readAt,
                        "unreadCount", unreadCount)));
    }

    public void notificationsReadAll(Long recipientId, LocalDateTime readAt, int updatedCount, long unreadCount) {
        afterCommit(() -> publisher.publish(
                recipientId,
                READ_ALL_EVENT,
                Map.of("readAt", readAt, "updatedCount", updatedCount, "unreadCount", unreadCount)));
    }

    private void afterCommit(Runnable action) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            action.run();
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                action.run();
            }
        });
    }
}

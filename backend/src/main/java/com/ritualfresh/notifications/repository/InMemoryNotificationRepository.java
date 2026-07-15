package com.ritualfresh.notifications.repository;

import com.ritualfresh.notifications.model.InAppNotification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryNotificationRepository implements NotificationRepository {
    private final List<InAppNotification> notifications = new ArrayList<>();
    private final AtomicLong sequenceIds = new AtomicLong(1);

    @Override
    public synchronized InAppNotification save(InAppNotification notification) {
        notification.assignIdIfMissing(sequenceIds.getAndIncrement());
        notifications.removeIf(current -> current.getId().equals(notification.getId()));
        notifications.add(notification);
        return notification;
    }

    @Override
    public synchronized List<InAppNotification> findRecentByRecipientId(Long recipientId, int limit) {
        return notifications.stream()
                .filter(notification -> notification.getRecipient().getId().equals(recipientId))
                .sorted(Comparator.comparing(InAppNotification::getCreatedAt).reversed()
                        .thenComparing(InAppNotification::getId, Comparator.reverseOrder()))
                .limit(limit)
                .toList();
    }

    @Override
    public synchronized long countUnreadByRecipientId(Long recipientId) {
        return notifications.stream()
                .filter(notification -> notification.getRecipient().getId().equals(recipientId))
                .filter(notification -> !notification.isRead())
                .count();
    }

    @Override
    public synchronized Optional<InAppNotification> findByIdAndRecipientId(Long notificationId, Long recipientId) {
        return notifications.stream()
                .filter(notification -> notification.getId().equals(notificationId))
                .filter(notification -> notification.getRecipient().getId().equals(recipientId))
                .findFirst();
    }

    @Override
    public synchronized Optional<InAppNotification> findByRecipientIdAndEventKey(Long recipientId, String eventKey) {
        return notifications.stream()
                .filter(notification -> notification.getRecipient().getId().equals(recipientId))
                .filter(notification -> notification.getEventKey().equals(eventKey))
                .findFirst();
    }

    @Override
    public synchronized int markAllRead(Long recipientId, LocalDateTime readAt) {
        int updated = 0;
        for (InAppNotification notification : notifications) {
            if (notification.getRecipient().getId().equals(recipientId) && !notification.isRead()) {
                notification.markRead(readAt);
                updated++;
            }
        }
        return updated;
    }
}

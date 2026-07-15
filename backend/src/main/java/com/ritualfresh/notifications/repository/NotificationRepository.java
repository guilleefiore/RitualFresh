package com.ritualfresh.notifications.repository;

import com.ritualfresh.notifications.model.InAppNotification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    InAppNotification save(InAppNotification notification);

    List<InAppNotification> findRecentByRecipientId(Long recipientId, int limit);

    long countUnreadByRecipientId(Long recipientId);

    Optional<InAppNotification> findByIdAndRecipientId(Long notificationId, Long recipientId);

    Optional<InAppNotification> findByRecipientIdAndEventKey(Long recipientId, String eventKey);

    int markAllRead(Long recipientId, LocalDateTime readAt);
}

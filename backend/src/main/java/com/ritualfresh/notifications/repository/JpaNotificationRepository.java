package com.ritualfresh.notifications.repository;

import com.ritualfresh.notifications.model.InAppNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaNotificationRepository implements NotificationRepository {
    private final NotificationJpaRepository jpaRepository;

    @Override
    public InAppNotification save(InAppNotification notification) {
        return jpaRepository.save(notification);
    }

    @Override
    public List<InAppNotification> findRecentByRecipientId(Long recipientId, int limit) {
        return jpaRepository.findByRecipient_IdOrderByCreatedAtDescIdDesc(
                recipientId,
                PageRequest.of(0, limit));
    }

    @Override
    public long countUnreadByRecipientId(Long recipientId) {
        return jpaRepository.countByRecipient_IdAndReadAtIsNull(recipientId);
    }

    @Override
    public Optional<InAppNotification> findByIdAndRecipientId(Long notificationId, Long recipientId) {
        return jpaRepository.findByIdAndRecipient_Id(notificationId, recipientId);
    }

    @Override
    public Optional<InAppNotification> findByRecipientIdAndEventKey(Long recipientId, String eventKey) {
        return jpaRepository.findByRecipient_IdAndEventKey(recipientId, eventKey);
    }

    @Override
    public int markAllRead(Long recipientId, LocalDateTime readAt) {
        return jpaRepository.markAllRead(recipientId, readAt);
    }
}

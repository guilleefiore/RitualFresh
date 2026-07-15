package com.ritualfresh.notifications.repository;

import com.ritualfresh.notifications.model.InAppNotification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationJpaRepository extends JpaRepository<InAppNotification, Long> {
    List<InAppNotification> findByRecipient_IdOrderByCreatedAtDescIdDesc(Long recipientId, Pageable pageable);

    long countByRecipient_IdAndReadAtIsNull(Long recipientId);

    Optional<InAppNotification> findByIdAndRecipient_Id(Long notificationId, Long recipientId);

    Optional<InAppNotification> findByRecipient_IdAndEventKey(Long recipientId, String eventKey);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update InAppNotification notification
               set notification.readAt = :readAt
             where notification.recipient.id = :recipientId
               and notification.readAt is null
            """)
    int markAllRead(
            @Param("recipientId") Long recipientId,
            @Param("readAt") LocalDateTime readAt);
}

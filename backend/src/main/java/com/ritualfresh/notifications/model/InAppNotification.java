package com.ritualfresh.notifications.model;

import com.ritualfresh.auth.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "in_app_notifications",
        indexes = {
                @Index(name = "idx_notifications_recipient_created", columnList = "recipient_id, created_at, id"),
                @Index(name = "idx_notifications_recipient_read", columnList = "recipient_id, read_at")
        },
        uniqueConstraints = @UniqueConstraint(
                name = "uk_notifications_recipient_event",
                columnNames = {"recipient_id", "event_key"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InAppNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private NotificationType type;

    @Column(nullable = false, length = 140)
    private String title;

    @Column(nullable = false, length = 500)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private NotificationResourceType resourceType;

    private Long resourceId;

    @Column(name = "event_key", nullable = false, length = 160)
    private String eventKey;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    public static InAppNotification create(
            User recipient,
            NotificationType type,
            String title,
            String message,
            NotificationResourceType resourceType,
            Long resourceId,
            String eventKey,
            LocalDateTime createdAt) {
        InAppNotification notification = new InAppNotification();
        notification.recipient = recipient;
        notification.type = type;
        notification.title = title;
        notification.message = message;
        notification.resourceType = resourceType;
        notification.resourceId = resourceId;
        notification.eventKey = eventKey;
        notification.createdAt = createdAt;
        return notification;
    }

    public void markRead(LocalDateTime now) {
        if (readAt == null) {
            readAt = now;
        }
    }

    public boolean isRead() {
        return readAt != null;
    }

    public void assignIdIfMissing(long id) {
        if (this.id == null) {
            this.id = id;
        }
    }
}

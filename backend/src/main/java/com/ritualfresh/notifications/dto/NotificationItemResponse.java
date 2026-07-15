package com.ritualfresh.notifications.dto;

import com.ritualfresh.notifications.model.InAppNotification;
import com.ritualfresh.notifications.model.NotificationResourceType;
import com.ritualfresh.notifications.model.NotificationType;

import java.time.LocalDateTime;

public record NotificationItemResponse(
        Long id,
        NotificationType type,
        String title,
        String message,
        NotificationResourceType resourceType,
        Long resourceId,
        LocalDateTime createdAt,
        LocalDateTime readAt,
        boolean read) {

    public static NotificationItemResponse from(InAppNotification notification) {
        return new NotificationItemResponse(
                notification.getId(),
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getResourceType(),
                notification.getResourceId(),
                notification.getCreatedAt(),
                notification.getReadAt(),
                notification.isRead());
    }
}

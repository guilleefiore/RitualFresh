package com.ritualfresh.notifications.dto;

import java.time.LocalDateTime;

public record MarkAllNotificationsReadResponse(
        int updatedCount,
        long unreadCount,
        LocalDateTime readAt) {
}

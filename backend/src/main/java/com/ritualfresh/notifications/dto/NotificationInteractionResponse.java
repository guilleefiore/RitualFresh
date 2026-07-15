package com.ritualfresh.notifications.dto;

public record NotificationInteractionResponse(
        NotificationItemResponse notification,
        long unreadCount,
        NotificationDestinationResponse destination) {
}

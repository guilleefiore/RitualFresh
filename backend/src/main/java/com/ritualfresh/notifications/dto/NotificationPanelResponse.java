package com.ritualfresh.notifications.dto;

import java.util.List;

public record NotificationPanelResponse(
        List<NotificationItemResponse> items,
        long unreadCount) {
}

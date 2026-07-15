package com.ritualfresh.notifications.controller;

import com.ritualfresh.notifications.dto.MarkAllNotificationsReadResponse;
import com.ritualfresh.notifications.dto.NotificationInteractionResponse;
import com.ritualfresh.notifications.dto.NotificationPanelResponse;
import com.ritualfresh.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/recent")
    public NotificationPanelResponse getMyRecentNotifications() {
        return notificationService.getMyRecentNotifications();
    }

    @PatchMapping("/{notificationId}/read")
    public NotificationInteractionResponse markAsRead(@PathVariable Long notificationId) {
        return notificationService.markAsRead(notificationId);
    }

    @PatchMapping("/read-all")
    public MarkAllNotificationsReadResponse markAllAsRead() {
        return notificationService.markAllAsRead();
    }
}

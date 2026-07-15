package com.ritualfresh.notifications.service;

import com.ritualfresh.auth.model.User;
import com.ritualfresh.notifications.dto.NotificationDestinationResponse;
import com.ritualfresh.notifications.model.InAppNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDestinationService {
    private final List<NotificationDestinationResolver> resolvers;

    public NotificationDestinationResponse resolve(InAppNotification notification, User recipient) {
        if (notification.getResourceType() == null || notification.getResourceId() == null) {
            return NotificationDestinationResponse.unavailable();
        }

        for (NotificationDestinationResolver resolver : resolvers) {
            if (!resolver.supports(notification.getResourceType())) {
                continue;
            }
            try {
                return resolver.resolve(recipient, notification.getResourceId())
                        .filter(this::isSafeRelativePath)
                        .map(NotificationDestinationResponse::available)
                        .orElseGet(NotificationDestinationResponse::unavailable);
            } catch (RuntimeException exception) {
                log.warn(
                        "No se pudo resolver el destino de la notificacion {} para el usuario {}.",
                        notification.getId(),
                        recipient.getId());
                return NotificationDestinationResponse.unavailable();
            }
        }
        return NotificationDestinationResponse.unavailable();
    }

    private boolean isSafeRelativePath(String path) {
        return path != null && path.startsWith("/") && !path.startsWith("//");
    }
}

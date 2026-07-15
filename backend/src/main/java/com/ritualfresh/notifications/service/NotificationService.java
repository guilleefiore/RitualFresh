package com.ritualfresh.notifications.service;

import com.ritualfresh.auth.model.User;
import com.ritualfresh.auth.repository.UserRepository;
import com.ritualfresh.auth.service.UserService;
import com.ritualfresh.notifications.dto.MarkAllNotificationsReadResponse;
import com.ritualfresh.notifications.dto.NotificationInteractionResponse;
import com.ritualfresh.notifications.dto.NotificationItemResponse;
import com.ritualfresh.notifications.dto.NotificationPanelResponse;
import com.ritualfresh.notifications.integration.NotificationCommand;
import com.ritualfresh.notifications.model.InAppNotification;
import com.ritualfresh.notifications.realtime.NotificationRealtimeDispatcher;
import com.ritualfresh.notifications.repository.NotificationRepository;
import com.ritualfresh.shared.exception.BusinessRuleException;
import com.ritualfresh.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final int PANEL_LIMIT = 20;

    private final UserService userService;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationDestinationService destinationService;
    private final NotificationRealtimeDispatcher realtimeDispatcher;

    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public NotificationPanelResponse getMyRecentNotifications() {
        User user = userService.getAuthenticatedUser();
        return new NotificationPanelResponse(
                notificationRepository.findRecentByRecipientId(user.getId(), PANEL_LIMIT).stream()
                        .map(NotificationItemResponse::from)
                        .toList(),
                notificationRepository.countUnreadByRecipientId(user.getId()));
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public NotificationInteractionResponse markAsRead(Long notificationId) {
        User user = userService.getAuthenticatedUser();
        InAppNotification notification = notificationRepository
                .findByIdAndRecipientId(notificationId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("La notificación indicada no existe."));

        LocalDateTime now = LocalDateTime.now();
        notification.markRead(now);
        notificationRepository.save(notification);
        long unreadCount = notificationRepository.countUnreadByRecipientId(user.getId());
        realtimeDispatcher.notificationRead(
                user.getId(),
                notification.getId(),
                notification.getReadAt(),
                unreadCount);

        return new NotificationInteractionResponse(
                NotificationItemResponse.from(notification),
                unreadCount,
                destinationService.resolve(notification, user));
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public MarkAllNotificationsReadResponse markAllAsRead() {
        User user = userService.getAuthenticatedUser();
        LocalDateTime now = LocalDateTime.now();
        int updatedCount = notificationRepository.markAllRead(user.getId(), now);
        long unreadCount = notificationRepository.countUnreadByRecipientId(user.getId());
        realtimeDispatcher.notificationsReadAll(user.getId(), now, updatedCount, unreadCount);
        return new MarkAllNotificationsReadResponse(updatedCount, unreadCount, now);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<NotificationItemResponse> createFromEvent(NotificationCommand command) {
        validateCommand(command);
        Optional<InAppNotification> existing = notificationRepository
                .findByRecipientIdAndEventKey(command.recipientId(), command.eventKey());
        if (existing.isPresent()) {
            return Optional.empty();
        }

        User recipient = userRepository.findById(command.recipientId())
                .orElseThrow(() -> new BusinessRuleException("El destinatario de la notificación no existe."));
        LocalDateTime createdAt = command.occurredAt() == null ? LocalDateTime.now() : command.occurredAt();
        InAppNotification notification = notificationRepository.save(InAppNotification.create(
                recipient,
                command.type(),
                command.title().trim(),
                command.message().trim(),
                command.resourceType(),
                command.resourceId(),
                command.eventKey().trim(),
                createdAt));
        NotificationItemResponse response = NotificationItemResponse.from(notification);
        long unreadCount = notificationRepository.countUnreadByRecipientId(recipient.getId());
        realtimeDispatcher.notificationCreated(recipient.getId(), response, unreadCount);
        return Optional.of(response);
    }

    private void validateCommand(NotificationCommand command) {
        if (command == null
                || command.recipientId() == null
                || command.type() == null
                || command.eventKey() == null
                || command.eventKey().isBlank()
                || command.title() == null
                || command.title().isBlank()
                || command.message() == null
                || command.message().isBlank()) {
            throw new BusinessRuleException("El evento de notificación está incompleto.");
        }
        if (command.eventKey().trim().length() > 160
                || command.title().trim().length() > 140
                || command.message().trim().length() > 500) {
            throw new BusinessRuleException("El evento de notificación supera la longitud permitida.");
        }
        if ((command.resourceType() == null) != (command.resourceId() == null)) {
            throw new BusinessRuleException("El recurso de la notificación está incompleto.");
        }
    }
}

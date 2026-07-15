package com.ritualfresh.notifications;

import com.ritualfresh.auth.model.User;
import com.ritualfresh.auth.model.UserRole;
import com.ritualfresh.auth.repository.InMemoryUserRepository;
import com.ritualfresh.auth.repository.InMemoryUserSessionRepository;
import com.ritualfresh.auth.repository.UserRepository;
import com.ritualfresh.auth.service.UserService;
import com.ritualfresh.notifications.dto.NotificationInteractionResponse;
import com.ritualfresh.notifications.dto.NotificationPanelResponse;
import com.ritualfresh.notifications.integration.NotificationCommand;
import com.ritualfresh.notifications.model.NotificationResourceType;
import com.ritualfresh.notifications.model.NotificationType;
import com.ritualfresh.notifications.realtime.NotificationRealtimeDispatcher;
import com.ritualfresh.notifications.realtime.NotificationRealtimePublisher;
import com.ritualfresh.notifications.repository.InMemoryNotificationRepository;
import com.ritualfresh.notifications.repository.NotificationRepository;
import com.ritualfresh.notifications.service.NotificationDestinationResolver;
import com.ritualfresh.notifications.service.NotificationDestinationService;
import com.ritualfresh.notifications.service.NotificationService;
import com.ritualfresh.profiles.repository.InMemoryClientProfileRepository;
import com.ritualfresh.profiles.repository.InMemoryWorkerProfileRepository;
import com.ritualfresh.shared.exception.ResourceNotFoundException;
import com.ritualfresh.shared.security.AuthenticatedUserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationServiceTest {
    private NotificationService notificationService;
    private NotificationRepository notificationRepository;
    private UserRepository userRepository;
    private CapturingRealtimePublisher realtimePublisher;
    private User client;
    private User worker;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        userRepository = new InMemoryUserRepository();
        UserService userService = new UserService(
                userRepository,
                new InMemoryUserSessionRepository(),
                new InMemoryAccountEmailService(),
                new InMemoryClientProfileRepository(),
                new InMemoryWorkerProfileRepository());
        notificationRepository = new InMemoryNotificationRepository();
        realtimePublisher = new CapturingRealtimePublisher();
        NotificationDestinationResolver contractResolver = new NotificationDestinationResolver() {
            @Override
            public boolean supports(NotificationResourceType resourceType) {
                return resourceType == NotificationResourceType.CONTRACT;
            }

            @Override
            public Optional<String> resolve(User recipient, Long resourceId) {
                return resourceId == 404L ? Optional.empty() : Optional.of("/contracts/" + resourceId);
            }
        };
        notificationService = new NotificationService(
                userService,
                userRepository,
                notificationRepository,
                new NotificationDestinationService(List.of(contractResolver)),
                new NotificationRealtimeDispatcher(realtimePublisher));
        client = user("Ana", "Cliente", "ana.notifications@example.com", UserRole.CLIENT);
        worker = user("Bruno", "Trabajador", "bruno.notifications@example.com", UserRole.WORKER);
    }

    @Test
    void returnsLatestTwentyButCountsEveryUnreadNotification() {
        LocalDateTime start = LocalDateTime.of(2026, 7, 1, 9, 0);
        for (int index = 0; index < 22; index++) {
            create(client, "event-" + index, (long) index, start.plusMinutes(index));
        }
        authenticate(client);

        NotificationPanelResponse panel = notificationService.getMyRecentNotifications();

        assertEquals(20, panel.items().size());
        assertEquals(22, panel.unreadCount());
        assertEquals("event title 21", panel.items().getFirst().title());
        assertEquals("event title 2", panel.items().getLast().title());
    }

    @Test
    void enforcesOwnershipAndKeepsReadOperationIdempotent() {
        var own = create(client, "own-event", 10L, LocalDateTime.now());
        var foreign = create(worker, "foreign-event", 11L, LocalDateTime.now().plusMinutes(1));
        authenticate(client);

        NotificationInteractionResponse firstRead = notificationService.markAsRead(own.id());
        NotificationInteractionResponse secondRead = notificationService.markAsRead(own.id());

        assertTrue(firstRead.notification().read());
        assertEquals(firstRead.notification().readAt(), secondRead.notification().readAt());
        assertEquals(0, secondRead.unreadCount());
        assertEquals("/contracts/10", firstRead.destination().path());
        assertThrows(ResourceNotFoundException.class, () -> notificationService.markAsRead(foreign.id()));
    }

    @Test
    void marksOnlyCurrentUsersNotificationsAndReportsUnavailableTargets() {
        var missingTarget = create(client, "missing-target", 404L, LocalDateTime.now());
        create(client, "second-own", 12L, LocalDateTime.now().plusMinutes(1));
        create(worker, "worker-event", 13L, LocalDateTime.now().plusMinutes(2));
        authenticate(client);

        NotificationInteractionResponse response = notificationService.markAsRead(missingTarget.id());
        assertFalse(response.destination().available());
        assertEquals("El contenido ya no se encuentra disponible.", response.destination().message());

        var markedAll = notificationService.markAllAsRead();
        assertEquals(1, markedAll.updatedCount());
        assertEquals(0, notificationRepository.countUnreadByRecipientId(client.getId()));
        assertEquals(1, notificationRepository.countUnreadByRecipientId(worker.getId()));
    }

    @Test
    void ignoresDuplicateEventsPerRecipientAndPublishesRealtimeUpdates() {
        NotificationCommand command = command(client, "contract-55-confirmed", 55L, LocalDateTime.now());

        assertTrue(notificationService.createFromEvent(command).isPresent());
        assertTrue(notificationService.createFromEvent(command).isEmpty());
        assertEquals(1, notificationRepository.countUnreadByRecipientId(client.getId()));
        assertEquals(1, realtimePublisher.events.stream()
                .filter(event -> event.type.equals(NotificationRealtimeDispatcher.CREATED_EVENT))
                .count());

        authenticate(client);
        notificationService.markAllAsRead();
        assertTrue(realtimePublisher.events.stream()
                .anyMatch(event -> event.type.equals(NotificationRealtimeDispatcher.READ_ALL_EVENT)));
    }

    private com.ritualfresh.notifications.dto.NotificationItemResponse create(
            User recipient,
            String eventKey,
            Long resourceId,
            LocalDateTime occurredAt) {
        return notificationService.createFromEvent(command(recipient, eventKey, resourceId, occurredAt)).orElseThrow();
    }

    private NotificationCommand command(User recipient, String eventKey, Long resourceId, LocalDateTime occurredAt) {
        String suffix = eventKey.startsWith("event-") ? eventKey.substring("event-".length()) : eventKey;
        return new NotificationCommand(
                eventKey,
                recipient.getId(),
                NotificationType.SERVICE_CONFIRMED,
                "event title " + suffix,
                "El servicio quedó confirmado.",
                NotificationResourceType.CONTRACT,
                resourceId,
                occurredAt);
    }

    private User user(String firstName, String lastName, String email, UserRole role) {
        User user = User.register(new User.RegistrationData(
                firstName,
                lastName,
                email,
                "hash",
                role,
                LocalDateTime.now(),
                "validation-" + email,
                LocalDateTime.now().plusDays(1)));
        user.validateAccount();
        return userRepository.save(user);
    }

    private void authenticate(User user) {
        var principal = AuthenticatedUserPrincipal.from(user);
        var authentication = new UsernamePasswordAuthenticationToken(
                principal,
                "test-token",
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private static final class CapturingRealtimePublisher implements NotificationRealtimePublisher {
        private final List<CapturedEvent> events = new ArrayList<>();

        @Override
        public void publish(Long recipientId, String type, Object payload) {
            events.add(new CapturedEvent(recipientId, type, payload));
        }
    }

    private record CapturedEvent(Long recipientId, String type, Object payload) {
    }
}

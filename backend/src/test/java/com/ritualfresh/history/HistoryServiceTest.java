package com.ritualfresh.history;

import com.ritualfresh.auth.model.User;
import com.ritualfresh.auth.model.UserRole;
import com.ritualfresh.auth.repository.InMemoryUserRepository;
import com.ritualfresh.auth.repository.InMemoryUserSessionRepository;
import com.ritualfresh.auth.repository.UserRepository;
import com.ritualfresh.auth.service.UserService;
import com.ritualfresh.history.dto.HistoryPageResponse;
import com.ritualfresh.history.model.ServiceHistoryRecord;
import com.ritualfresh.history.model.ServiceHistoryStatus;
import com.ritualfresh.history.repository.InMemoryServiceHistoryRecordRepository;
import com.ritualfresh.history.repository.ServiceHistoryRecordRepository;
import com.ritualfresh.history.service.HistoryService;
import com.ritualfresh.notifications.InMemoryAccountEmailService;
import com.ritualfresh.profiles.repository.InMemoryClientProfileRepository;
import com.ritualfresh.profiles.repository.InMemoryWorkerProfileRepository;
import com.ritualfresh.shared.exception.BusinessRuleException;
import com.ritualfresh.shared.security.AuthenticatedUserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HistoryServiceTest {
    private HistoryService historyService;
    private ServiceHistoryRecordRepository historyRepository;
    private UserRepository userRepository;
    private User client;
    private User otherClient;
    private User worker;
    private User otherWorker;

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
        historyRepository = new InMemoryServiceHistoryRecordRepository();
        historyService = new HistoryService(userService, historyRepository);
        client = user("Ana", "Cliente", "ana@example.com", UserRole.CLIENT);
        otherClient = user("Belen", "Cliente", "belen@example.com", UserRole.CLIENT);
        worker = user("Carlos", "Trabajador", "carlos@example.com", UserRole.WORKER);
        otherWorker = user("Diego", "Trabajador", "diego@example.com", UserRole.WORKER);
    }

    @Test
    void returnsOnlyOwnedRecordsWithRoleSpecificCounterpart() {
        save(client, worker, LocalDateTime.of(2026, 7, 10, 10, 0), ServiceHistoryStatus.COMPLETED);
        save(otherClient, worker, LocalDateTime.of(2026, 7, 11, 10, 0), ServiceHistoryStatus.COMPLETED);
        save(client, otherWorker, LocalDateTime.of(2026, 7, 12, 10, 0), ServiceHistoryStatus.PENDING);

        authenticate(client);
        HistoryPageResponse clientHistory = historyService.getMyHistory(null, null, null, 0, 20);
        assertEquals(2, clientHistory.totalElements());
        assertEquals("Diego Trabajador", clientHistory.content().getFirst().counterpartName());

        authenticate(worker);
        HistoryPageResponse workerHistory = historyService.getMyHistory(null, null, null, 0, 20);
        assertEquals(2, workerHistory.totalElements());
        assertEquals("Belen Cliente", workerHistory.content().getFirst().counterpartName());
    }

    @Test
    void filtersStatusAndInclusiveDateRangeAndKeepsNewestFirst() {
        save(client, worker, LocalDateTime.of(2026, 7, 1, 23, 59), ServiceHistoryStatus.COMPLETED);
        save(client, worker, LocalDateTime.of(2026, 7, 2, 12, 0), ServiceHistoryStatus.CANCELLED);
        save(client, worker, LocalDateTime.of(2026, 7, 3, 0, 0), ServiceHistoryStatus.COMPLETED);
        save(client, worker, LocalDateTime.of(2026, 7, 4, 0, 0), ServiceHistoryStatus.COMPLETED);
        authenticate(client);

        HistoryPageResponse result = historyService.getMyHistory(
                ServiceHistoryStatus.COMPLETED,
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 3),
                0,
                20);

        assertEquals(2, result.totalElements());
        assertEquals(LocalDate.of(2026, 7, 3), result.content().get(0).scheduledAt().toLocalDate());
        assertEquals(LocalDate.of(2026, 7, 1), result.content().get(1).scheduledAt().toLocalDate());
    }

    @Test
    void paginatesAtTwentyItemsAndReportsEmptyPages() {
        for (int day = 1; day <= 22; day++) {
            save(client, worker, LocalDateTime.of(2026, 6, day, 10, 0), ServiceHistoryStatus.PENDING);
        }
        authenticate(client);

        HistoryPageResponse firstPage = historyService.getMyHistory(null, null, null, 0, 100);
        HistoryPageResponse secondPage = historyService.getMyHistory(null, null, null, 1, 100);

        assertEquals(20, firstPage.content().size());
        assertTrue(firstPage.hasNext());
        assertEquals(2, secondPage.content().size());
        assertEquals(22, secondPage.totalElements());

        authenticate(otherClient);
        assertTrue(historyService.getMyHistory(null, null, null, 0, 20).content().isEmpty());
    }

    @Test
    void rejectsInvertedDateRange() {
        authenticate(client);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () ->
                historyService.getMyHistory(
                        null,
                        LocalDate.of(2026, 7, 10),
                        LocalDate.of(2026, 7, 9),
                        0,
                        20));

        assertEquals("La fecha desde no puede ser posterior a la fecha hasta.", exception.getMessage());
    }

    private ServiceHistoryRecord save(
            User recordClient,
            User recordWorker,
            LocalDateTime scheduledAt,
            ServiceHistoryStatus status) {
        return historyRepository.save(ServiceHistoryRecord.create(
                recordClient,
                recordWorker,
                "Limpieza general",
                "Limpieza",
                scheduledAt,
                status,
                new BigDecimal("15000.00"),
                status == ServiceHistoryStatus.COMPLETED ? 5 : null));
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
}

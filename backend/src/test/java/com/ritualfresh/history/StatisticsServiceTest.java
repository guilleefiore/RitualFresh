package com.ritualfresh.history;

import com.ritualfresh.auth.model.User;
import com.ritualfresh.auth.model.UserRole;
import com.ritualfresh.auth.repository.InMemoryUserRepository;
import com.ritualfresh.auth.repository.InMemoryUserSessionRepository;
import com.ritualfresh.auth.repository.UserRepository;
import com.ritualfresh.auth.service.UserService;
import com.ritualfresh.history.dto.ClientStatisticsResponse;
import com.ritualfresh.history.dto.StatisticsPeriod;
import com.ritualfresh.history.dto.WorkerStatisticsResponse;
import com.ritualfresh.history.model.ServiceHistoryRecord;
import com.ritualfresh.history.model.ServiceHistoryStatus;
import com.ritualfresh.history.repository.InMemoryServiceHistoryRecordRepository;
import com.ritualfresh.history.repository.ServiceHistoryRecordRepository;
import com.ritualfresh.history.service.StatisticsService;
import com.ritualfresh.notifications.InMemoryAccountEmailService;
import com.ritualfresh.profiles.repository.InMemoryClientProfileRepository;
import com.ritualfresh.profiles.repository.InMemoryWorkerProfileRepository;
import com.ritualfresh.shared.security.AuthenticatedUserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StatisticsServiceTest {
    private static final LocalDate TODAY = LocalDate.of(2026, 7, 15);

    private StatisticsService statisticsService;
    private ServiceHistoryRecordRepository historyRepository;
    private UserRepository userRepository;
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
        historyRepository = new InMemoryServiceHistoryRecordRepository();
        Clock clock = Clock.fixed(Instant.parse("2026-07-15T15:00:00Z"), ZoneId.of("America/Argentina/Mendoza"));
        statisticsService = new StatisticsService(userService, historyRepository, clock);
        client = user("Ana", "Cliente", "ana.stats@example.com", UserRole.CLIENT);
        worker = user("Zoe", "Trabajadora", "zoe.stats@example.com", UserRole.WORKER);
    }

    @Test
    void workerMetricsUseMobileWindowsCompletedJobsAndRatedRecordsOnly() {
        save(client, worker, TODAY.minusDays(29), ServiceHistoryStatus.COMPLETED, "Limpieza", new BigDecimal("1000"), 4);
        save(client, worker, TODAY, ServiceHistoryStatus.COMPLETED, "Limpieza", null, null);
        save(client, worker, TODAY.minusDays(2), ServiceHistoryStatus.CANCELLED, "Limpieza", new BigDecimal("9000"), 1);
        save(client, worker, TODAY.minusDays(30), ServiceHistoryStatus.COMPLETED, "Limpieza", new BigDecimal("1000"), 1);
        authenticate(worker);

        WorkerStatisticsResponse result = statisticsService.getMyWorkerStatistics(StatisticsPeriod.LAST_30_DAYS);

        assertEquals(TODAY.minusDays(29), result.from());
        assertEquals(TODAY, result.to());
        assertEquals(2, result.completedJobs());
        assertEquals(new BigDecimal("4.00"), result.averageRating());
        assertEquals(5, result.completedJobsTimeline().size());
        assertEquals(2, result.completedJobsTimeline().stream().mapToLong(bucket -> bucket.count()).sum());
    }

    @Test
    void workerAverageIsNullWhenCompletedJobsHaveNoRatingsAndSevenDaysAreDaily() {
        save(client, worker, TODAY, ServiceHistoryStatus.COMPLETED, "Limpieza", null, null);
        authenticate(worker);

        WorkerStatisticsResponse result = statisticsService.getMyWorkerStatistics(StatisticsPeriod.LAST_7_DAYS);

        assertNull(result.averageRating());
        assertEquals(7, result.completedJobsTimeline().size());
        assertEquals(TODAY.minusDays(6), result.completedJobsTimeline().getFirst().from());
    }

    @Test
    void clientMetricsExcludeCancelledAndUseOnlyCompletedForSpendCategoriesAndWorkers() {
        User workerA = user("Ana", "Alvarez", "worker.a@example.com", UserRole.WORKER);
        User workerB = user("Bruno", "Benitez", "worker.b@example.com", UserRole.WORKER);
        save(client, workerA, TODAY.minusDays(3), ServiceHistoryStatus.PENDING, "Plomeria", new BigDecimal("500"), null);
        save(client, workerA, TODAY.minusDays(2), ServiceHistoryStatus.COMPLETED, "Limpieza", new BigDecimal("1200"), 5);
        save(client, workerA, TODAY.minusDays(1), ServiceHistoryStatus.COMPLETED, "Limpieza", null, 4);
        save(client, workerB, TODAY, ServiceHistoryStatus.COMPLETED, "Jardineria", new BigDecimal("800"), 5);
        save(client, workerB, TODAY, ServiceHistoryStatus.CANCELLED, "Electricidad", new BigDecimal("9999"), 1);
        authenticate(client);

        ClientStatisticsResponse result = statisticsService.getMyClientStatistics(StatisticsPeriod.LAST_7_DAYS);

        assertEquals(4, result.hiredServices());
        assertEquals(1, result.pendingServices());
        assertEquals(3, result.completedServices());
        assertEquals(new BigDecimal("2000"), result.totalSpentArs());
        assertEquals(List.of("Limpieza", "Jardineria"), result.categories().stream().map(metric -> metric.category()).toList());
        assertEquals("Ana Alvarez", result.frequentWorkers().getFirst().workerName());
        assertEquals(2, result.frequentWorkers().getFirst().completedServices());
        assertEquals(new BigDecimal("2000"), result.spendingTimeline().stream()
                .map(bucket -> bucket.amountArs())
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    @Test
    void frequentWorkersAreOrderedByCountThenNameAndLimitedToFive() {
        List<User> workers = new ArrayList<>();
        for (int index = 0; index < 6; index++) {
            workers.add(user("Worker" + index, "Apellido", "worker" + index + "@example.com", UserRole.WORKER));
        }
        for (User current : workers) {
            save(client, current, TODAY, ServiceHistoryStatus.COMPLETED, "Limpieza", BigDecimal.ONE, null);
        }
        save(client, workers.get(5), TODAY, ServiceHistoryStatus.COMPLETED, "Limpieza", BigDecimal.ONE, null);
        authenticate(client);

        ClientStatisticsResponse result = statisticsService.getMyClientStatistics(StatisticsPeriod.LAST_7_DAYS);

        assertEquals(5, result.frequentWorkers().size());
        assertEquals(workers.get(5).getId(), result.frequentWorkers().getFirst().workerId());
        assertEquals("Worker0 Apellido", result.frequentWorkers().get(1).workerName());
    }

    @Test
    void annualWindowIsGroupedByCalendarMonthSegments() {
        authenticate(worker);

        WorkerStatisticsResponse result = statisticsService.getMyWorkerStatistics(StatisticsPeriod.LAST_365_DAYS);

        assertEquals(TODAY.minusDays(364), result.from());
        assertEquals(13, result.completedJobsTimeline().size());
        assertEquals(result.from(), result.completedJobsTimeline().getFirst().from());
        assertEquals(TODAY, result.completedJobsTimeline().getLast().to());
    }

    private ServiceHistoryRecord save(
            User recordClient,
            User recordWorker,
            LocalDate date,
            ServiceHistoryStatus status,
            String category,
            BigDecimal amount,
            Integer rating) {
        return historyRepository.save(ServiceHistoryRecord.create(
                recordClient,
                recordWorker,
                category + " del hogar",
                category,
                date.atTime(10, 0),
                status,
                amount,
                rating));
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

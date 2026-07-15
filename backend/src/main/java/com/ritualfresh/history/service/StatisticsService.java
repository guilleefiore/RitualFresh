package com.ritualfresh.history.service;

import com.ritualfresh.auth.model.User;
import com.ritualfresh.auth.model.UserRole;
import com.ritualfresh.auth.service.UserService;
import com.ritualfresh.history.dto.CategoryMetricResponse;
import com.ritualfresh.history.dto.ClientStatisticsResponse;
import com.ritualfresh.history.dto.FrequentWorkerResponse;
import com.ritualfresh.history.dto.StatisticsPeriod;
import com.ritualfresh.history.dto.TimeBucketResponse;
import com.ritualfresh.history.dto.WorkerStatisticsResponse;
import com.ritualfresh.history.model.ServiceHistoryRecord;
import com.ritualfresh.history.model.ServiceHistoryStatus;
import com.ritualfresh.history.repository.ServiceHistoryRecordRepository;
import com.ritualfresh.shared.exception.BusinessRuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class StatisticsService {
    private final UserService userService;
    private final ServiceHistoryRecordRepository historyRepository;
    private final Clock clock;

    @Autowired
    public StatisticsService(UserService userService, ServiceHistoryRecordRepository historyRepository) {
        this(userService, historyRepository, Clock.systemDefaultZone());
    }

    public StatisticsService(UserService userService, ServiceHistoryRecordRepository historyRepository, Clock clock) {
        this.userService = userService;
        this.historyRepository = historyRepository;
        this.clock = clock;
    }

    @PreAuthorize("hasRole('WORKER')")
    @Transactional(readOnly = true)
    public WorkerStatisticsResponse getMyWorkerStatistics(StatisticsPeriod requestedPeriod) {
        User worker = requireRole(UserRole.WORKER);
        PeriodWindow window = window(requestedPeriod);
        List<ServiceHistoryRecord> completed = historyRepository
                .findForStatistics(worker.getId(), UserRole.WORKER, window.from(), window.to()).stream()
                .filter(record -> record.getStatus() == ServiceHistoryStatus.COMPLETED)
                .toList();

        List<Integer> ratings = completed.stream()
                .map(ServiceHistoryRecord::getWorkerRating)
                .filter(java.util.Objects::nonNull)
                .toList();
        BigDecimal averageRating = ratings.isEmpty()
                ? null
                : BigDecimal.valueOf(ratings.stream().mapToInt(Integer::intValue).sum())
                        .divide(BigDecimal.valueOf(ratings.size()), 2, RoundingMode.HALF_UP);

        return new WorkerStatisticsResponse(
                window.period(),
                window.from(),
                window.to(),
                completed.size(),
                averageRating,
                buckets(window).stream()
                        .map(range -> workerBucket(range, completed))
                        .toList());
    }

    @PreAuthorize("hasRole('CLIENT')")
    @Transactional(readOnly = true)
    public ClientStatisticsResponse getMyClientStatistics(StatisticsPeriod requestedPeriod) {
        User client = requireRole(UserRole.CLIENT);
        PeriodWindow window = window(requestedPeriod);
        List<ServiceHistoryRecord> effectiveRecords = historyRepository
                .findForStatistics(client.getId(), UserRole.CLIENT, window.from(), window.to()).stream()
                .filter(record -> record.getStatus() != ServiceHistoryStatus.CANCELLED)
                .toList();
        List<ServiceHistoryRecord> completed = effectiveRecords.stream()
                .filter(record -> record.getStatus() == ServiceHistoryStatus.COMPLETED)
                .toList();

        long pending = effectiveRecords.stream()
                .filter(record -> record.getStatus() == ServiceHistoryStatus.PENDING)
                .count();
        BigDecimal totalSpent = completed.stream()
                .map(ServiceHistoryRecord::getAmountArs)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ClientStatisticsResponse(
                window.period(),
                window.from(),
                window.to(),
                effectiveRecords.size(),
                pending,
                completed.size(),
                totalSpent,
                buckets(window).stream()
                        .map(range -> clientBucket(range, completed))
                        .toList(),
                categoryMetrics(completed),
                frequentWorkers(completed));
    }

    private User requireRole(UserRole expectedRole) {
        User user = userService.getAuthenticatedUser();
        if (user.getRole() != expectedRole) {
            throw new BusinessRuleException("El rol del usuario no permite consultar estas estadisticas.");
        }
        return user;
    }

    private PeriodWindow window(StatisticsPeriod requestedPeriod) {
        StatisticsPeriod period = requestedPeriod == null ? StatisticsPeriod.LAST_30_DAYS : requestedPeriod;
        LocalDate today = LocalDate.now(clock);
        return new PeriodWindow(period, today.minusDays(period.days() - 1L), today);
    }

    private List<DateRange> buckets(PeriodWindow window) {
        List<DateRange> ranges = new ArrayList<>();
        LocalDate cursor = window.from();
        if (window.period() == StatisticsPeriod.LAST_7_DAYS) {
            while (!cursor.isAfter(window.to())) {
                ranges.add(new DateRange(cursor, cursor));
                cursor = cursor.plusDays(1);
            }
            return ranges;
        }
        if (window.period() == StatisticsPeriod.LAST_30_DAYS) {
            while (!cursor.isAfter(window.to())) {
                LocalDate end = min(cursor.plusDays(6), window.to());
                ranges.add(new DateRange(cursor, end));
                cursor = end.plusDays(1);
            }
            return ranges;
        }
        while (!cursor.isAfter(window.to())) {
            LocalDate end = min(cursor.with(TemporalAdjusters.lastDayOfMonth()), window.to());
            ranges.add(new DateRange(cursor, end));
            cursor = end.plusDays(1);
        }
        return ranges;
    }

    private TimeBucketResponse workerBucket(DateRange range, List<ServiceHistoryRecord> records) {
        long count = records.stream().filter(record -> range.contains(record.getScheduledAt().toLocalDate())).count();
        return new TimeBucketResponse(range.from(), range.to(), count, BigDecimal.ZERO);
    }

    private TimeBucketResponse clientBucket(DateRange range, List<ServiceHistoryRecord> records) {
        List<ServiceHistoryRecord> matching = records.stream()
                .filter(record -> range.contains(record.getScheduledAt().toLocalDate()))
                .toList();
        BigDecimal amount = matching.stream()
                .map(ServiceHistoryRecord::getAmountArs)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new TimeBucketResponse(range.from(), range.to(), matching.size(), amount);
    }

    private List<CategoryMetricResponse> categoryMetrics(List<ServiceHistoryRecord> completed) {
        return countBy(completed, ServiceHistoryRecord::getCategory).entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
                .map(entry -> new CategoryMetricResponse(entry.getKey(), entry.getValue()))
                .toList();
    }

    private List<FrequentWorkerResponse> frequentWorkers(List<ServiceHistoryRecord> completed) {
        Map<Long, WorkerCount> workers = new LinkedHashMap<>();
        for (ServiceHistoryRecord record : completed) {
            Long workerId = record.getWorker().getId();
            String workerName = (record.getWorker().getFirstName() + " " + record.getWorker().getLastName()).trim();
            workers.compute(workerId, (id, current) -> current == null
                    ? new WorkerCount(workerId, workerName, 1)
                    : new WorkerCount(workerId, current.name(), current.count() + 1));
        }
        return workers.values().stream()
                .sorted(Comparator.comparingLong(WorkerCount::count).reversed()
                        .thenComparing(WorkerCount::name)
                        .thenComparing(WorkerCount::id))
                .limit(5)
                .map(worker -> new FrequentWorkerResponse(worker.id(), worker.name(), worker.count()))
                .toList();
    }

    private <T> Map<T, Long> countBy(List<ServiceHistoryRecord> records, Function<ServiceHistoryRecord, T> classifier) {
        Map<T, Long> result = new LinkedHashMap<>();
        records.forEach(record -> result.merge(classifier.apply(record), 1L, Long::sum));
        return result;
    }

    private LocalDate min(LocalDate first, LocalDate second) {
        return first.isBefore(second) ? first : second;
    }

    private record PeriodWindow(StatisticsPeriod period, LocalDate from, LocalDate to) {
    }

    private record DateRange(LocalDate from, LocalDate to) {
        boolean contains(LocalDate date) {
            return !date.isBefore(from) && !date.isAfter(to);
        }
    }

    private record WorkerCount(Long id, String name, long count) {
    }
}

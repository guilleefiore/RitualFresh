package com.ritualfresh.history.repository;

import com.ritualfresh.auth.model.UserRole;
import com.ritualfresh.history.model.ServiceHistoryRecord;
import com.ritualfresh.history.model.ServiceHistoryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryServiceHistoryRecordRepository implements ServiceHistoryRecordRepository {
    private final List<ServiceHistoryRecord> records = new ArrayList<>();
    private final AtomicLong sequenceIds = new AtomicLong(1);

    @Override
    public ServiceHistoryRecord save(ServiceHistoryRecord record) {
        record.assignIdIfMissing(sequenceIds.getAndIncrement());
        records.removeIf(current -> current.getId().equals(record.getId()));
        records.add(record);
        return record;
    }

    @Override
    public Page<ServiceHistoryRecord> findHistory(
            Long userId,
            UserRole role,
            ServiceHistoryStatus status,
            LocalDate from,
            LocalDate to,
            Pageable pageable) {
        List<ServiceHistoryRecord> matching = filtered(userId, role, status, from, to).stream()
                .sorted(Comparator.comparing(ServiceHistoryRecord::getScheduledAt).reversed()
                        .thenComparing(ServiceHistoryRecord::getId, Comparator.reverseOrder()))
                .toList();
        int start = Math.min((int) pageable.getOffset(), matching.size());
        int end = Math.min(start + pageable.getPageSize(), matching.size());
        return new PageImpl<>(matching.subList(start, end), pageable, matching.size());
    }

    @Override
    public List<ServiceHistoryRecord> findForStatistics(
            Long userId,
            UserRole role,
            LocalDate from,
            LocalDate to) {
        return filtered(userId, role, null, from, to).stream()
                .sorted(Comparator.comparing(ServiceHistoryRecord::getScheduledAt))
                .toList();
    }

    private List<ServiceHistoryRecord> filtered(
            Long userId,
            UserRole role,
            ServiceHistoryStatus status,
            LocalDate from,
            LocalDate to) {
        return records.stream()
                .filter(record -> owns(record, userId, role))
                .filter(record -> status == null || record.getStatus() == status)
                .filter(record -> from == null || !record.getScheduledAt().toLocalDate().isBefore(from))
                .filter(record -> to == null || !record.getScheduledAt().toLocalDate().isAfter(to))
                .toList();
    }

    private boolean owns(ServiceHistoryRecord record, Long userId, UserRole role) {
        return role == UserRole.WORKER
                ? record.getWorker().getId().equals(userId)
                : record.getClient().getId().equals(userId);
    }
}

package com.ritualfresh.history.repository;

import com.ritualfresh.auth.model.UserRole;
import com.ritualfresh.history.model.ServiceHistoryRecord;
import com.ritualfresh.history.model.ServiceHistoryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface ServiceHistoryRecordRepository {
    ServiceHistoryRecord save(ServiceHistoryRecord record);

    Page<ServiceHistoryRecord> findHistory(
            Long userId,
            UserRole role,
            ServiceHistoryStatus status,
            LocalDate from,
            LocalDate to,
            Pageable pageable);

    List<ServiceHistoryRecord> findForStatistics(
            Long userId,
            UserRole role,
            LocalDate from,
            LocalDate to);
}

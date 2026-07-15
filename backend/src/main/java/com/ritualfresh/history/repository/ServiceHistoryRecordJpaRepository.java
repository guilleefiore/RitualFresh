package com.ritualfresh.history.repository;

import com.ritualfresh.history.model.ServiceHistoryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServiceHistoryRecordJpaRepository
        extends JpaRepository<ServiceHistoryRecord, Long>, JpaSpecificationExecutor<ServiceHistoryRecord> {
}

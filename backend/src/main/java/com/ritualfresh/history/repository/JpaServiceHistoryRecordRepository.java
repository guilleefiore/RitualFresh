package com.ritualfresh.history.repository;

import com.ritualfresh.auth.model.UserRole;
import com.ritualfresh.history.model.ServiceHistoryRecord;
import com.ritualfresh.history.model.ServiceHistoryStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaServiceHistoryRecordRepository implements ServiceHistoryRecordRepository {
    private final ServiceHistoryRecordJpaRepository jpaRepository;

    @Override
    public ServiceHistoryRecord save(ServiceHistoryRecord record) {
        return jpaRepository.save(record);
    }

    @Override
    public Page<ServiceHistoryRecord> findHistory(
            Long userId,
            UserRole role,
            ServiceHistoryStatus status,
            LocalDate from,
            LocalDate to,
            Pageable pageable) {
        return jpaRepository.findAll(filters(userId, role, status, from, to), pageable);
    }

    @Override
    public List<ServiceHistoryRecord> findForStatistics(
            Long userId,
            UserRole role,
            LocalDate from,
            LocalDate to) {
        return jpaRepository.findAll(
                filters(userId, role, null, from, to),
                Sort.by(Sort.Direction.ASC, "scheduledAt"));
    }

    private Specification<ServiceHistoryRecord> filters(
            Long userId,
            UserRole role,
            ServiceHistoryStatus status,
            LocalDate from,
            LocalDate to) {
        return (root, query, builder) -> {
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();
            String ownerAttribute = role == UserRole.WORKER ? "worker" : "client";
            predicates.add(builder.equal(root.get(ownerAttribute).get("id"), userId));
            if (status != null) {
                predicates.add(builder.equal(root.get("status"), status));
            }
            if (from != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("scheduledAt"), from.atStartOfDay()));
            }
            if (to != null) {
                predicates.add(builder.lessThan(root.get("scheduledAt"), to.plusDays(1).atStartOfDay()));
            }
            return builder.and(predicates.toArray(jakarta.persistence.criteria.Predicate[]::new));
        };
    }
}

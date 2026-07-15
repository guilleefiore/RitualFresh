package com.ritualfresh.admin.repository;

import com.ritualfresh.admin.model.AdminUserStatusChange;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminStatusChangeJpaRepository extends JpaRepository<AdminUserStatusChange, Long> {
    Page<AdminUserStatusChange> findByTargetUserId(Long targetUserId, Pageable pageable);
}

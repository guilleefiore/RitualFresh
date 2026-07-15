package com.ritualfresh.admin.repository;

import com.ritualfresh.admin.model.AdminUserStatusChange;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminStatusChangeRepository {
    AdminUserStatusChange save(AdminUserStatusChange change);

    Page<AdminUserStatusChange> findByTargetUserId(Long targetUserId, Pageable pageable);
}

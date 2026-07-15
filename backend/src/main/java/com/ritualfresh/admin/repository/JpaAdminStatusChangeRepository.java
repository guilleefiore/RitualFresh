package com.ritualfresh.admin.repository;

import com.ritualfresh.admin.model.AdminUserStatusChange;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaAdminStatusChangeRepository implements AdminStatusChangeRepository {
    private final AdminStatusChangeJpaRepository repository;

    @Override
    public AdminUserStatusChange save(AdminUserStatusChange change) {
        return repository.save(change);
    }

    @Override
    public Page<AdminUserStatusChange> findByTargetUserId(Long targetUserId, Pageable pageable) {
        return repository.findByTargetUserId(targetUserId, pageable);
    }
}

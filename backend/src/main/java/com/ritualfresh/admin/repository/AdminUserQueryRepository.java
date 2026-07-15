package com.ritualfresh.admin.repository;

import com.ritualfresh.auth.model.AccountStatus;
import com.ritualfresh.auth.model.User;
import com.ritualfresh.auth.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminUserQueryRepository {
    Page<User> search(String query, UserRole role, AccountStatus status, Pageable pageable);

    long countAll();

    long countByRole(UserRole role);

    long countByStatus(AccountStatus status);
}

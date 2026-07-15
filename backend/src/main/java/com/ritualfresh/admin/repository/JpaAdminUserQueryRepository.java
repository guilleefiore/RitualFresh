package com.ritualfresh.admin.repository;

import com.ritualfresh.auth.model.AccountStatus;
import com.ritualfresh.auth.model.User;
import com.ritualfresh.auth.model.UserRole;
import com.ritualfresh.auth.repository.UserJpaRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Repository
@RequiredArgsConstructor
public class JpaAdminUserQueryRepository implements AdminUserQueryRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    public Page<User> search(String query, UserRole role, AccountStatus status, Pageable pageable) {
        Specification<User> specification = (root, criteriaQuery, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.notEqual(root.get("role"), UserRole.ADMIN));

            if (query != null && !query.isBlank()) {
                String pattern = "%" + query.trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(builder.or(
                        builder.like(builder.lower(root.get("firstName")), pattern),
                        builder.like(builder.lower(root.get("lastName")), pattern),
                        builder.like(builder.lower(root.get("email")), pattern)));
            }

            if (role != null) {
                predicates.add(builder.equal(root.get("role"), role));
            }

            if (status != null) {
                predicates.add(builder.equal(root.get("accountStatus"), status));
            }

            return builder.and(predicates.toArray(Predicate[]::new));
        };

        return userJpaRepository.findAll(specification, pageable);
    }

    @Override
    public long countAll() {
        return userJpaRepository.count();
    }

    @Override
    public long countByRole(UserRole role) {
        return userJpaRepository.countByRole(role);
    }

    @Override
    public long countByStatus(AccountStatus status) {
        return userJpaRepository.countByAccountStatus(status);
    }
}

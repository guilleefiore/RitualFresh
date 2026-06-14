package com.ritualfresh.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ritualfresh.auth.model.User;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByAccountValidationToken(String tokenValidationToken);

    Optional<User> findByPasswordResetToken(String passwordResetToken);

    boolean existsByEmail(String email);
}

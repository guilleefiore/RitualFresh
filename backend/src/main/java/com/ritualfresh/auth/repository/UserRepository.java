package com.ritualfresh.auth.repository;

import com.ritualfresh.auth.model.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByAccountValidationToken(String token);

    Optional<User> findByPasswordResetToken(String token);

    boolean existsByEmail(String email);
}

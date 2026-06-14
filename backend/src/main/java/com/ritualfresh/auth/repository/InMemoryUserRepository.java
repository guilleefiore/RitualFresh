package com.ritualfresh.auth.repository;

import com.ritualfresh.auth.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> usersByEmail = new HashMap<>();
    private final Map<Long, User> usersById = new HashMap<>();
    private final AtomicLong sequenceIds = new AtomicLong(1);

    @Override
    public User save(User user) {
        user.assignIdIfMissing(sequenceIds.getAndIncrement());
        usersByEmail.put(normalizeEmail(user.getEmail()), user);
        usersById.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(usersById.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(usersByEmail.get(normalizeEmail(email)));
    }

    @Override
    public Optional<User> findByAccountValidationToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        return usersByEmail.values().stream()
                .filter(user -> token.equals(user.getAccountValidationToken()))
                .findFirst();
    }

    @Override
    public Optional<User> findByPasswordResetToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        return usersByEmail.values().stream()
                .filter(user -> token.equals(user.getPasswordResetToken()))
                .findFirst();
    }

    @Override
    public boolean existsByEmail(String email) {
        return usersByEmail.containsKey(normalizeEmail(email));
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}

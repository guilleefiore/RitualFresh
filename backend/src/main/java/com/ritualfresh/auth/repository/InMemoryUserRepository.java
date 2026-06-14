package com.ritualfresh.auth.repository;

import com.ritualfresh.auth.model.User;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> usersByEmail = new HashMap<>();
    private final Map<Long, User> usersById = new HashMap<>();
    private final AtomicLong sequenceIds = new AtomicLong(1);

    @Override
    public User save(User user) {
        assignIdIfMissing(user, sequenceIds.getAndIncrement());
        usersByEmail.put(normalizeEmail(user.getEmail()), user);
        usersById.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(usersById.values());
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

    private void assignIdIfMissing(User user, long id) {
        if (user.getId() != null) {
            return;
        }

        try {
            Field field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, id);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("No se pudo asignar el identificador en memoria.", exception);
        }
    }
}

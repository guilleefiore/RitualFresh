package com.ritualfresh.auth.repository;

import com.ritualfresh.auth.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaUserRepository implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    public JpaUserRepository(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        return userJpaRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(normalizeEmail(email));
    }

    @Override
    public Optional<User> findByAccountValidationToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        return userJpaRepository.findByAccountValidationToken(token);
    }

    @Override
    public Optional<User> findByPasswordResetToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        return userJpaRepository.findByPasswordResetToken(token);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(normalizeEmail(email));
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}

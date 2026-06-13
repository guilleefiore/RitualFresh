package com.ritualfresh.auth;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaUserSessionRepository implements UserSessionRepository {
    private final UserSessionJpaRepository userSessionJpaRepository;

    public JpaUserSessionRepository(UserSessionJpaRepository userSessionJpaRepository) {
        this.userSessionJpaRepository = userSessionJpaRepository;
    }

    @Override
    public UserSession save(UserSession session) {
        return userSessionJpaRepository.save(session);
    }

    @Override
    public Optional<UserSession> findByToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        return userSessionJpaRepository.findByToken(token.trim());
    }
}

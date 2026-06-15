package com.ritualfresh.auth.repository;

import com.ritualfresh.auth.model.UserSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaUserSessionRepository implements UserSessionRepository {
    private final UserSessionJpaRepository userSessionJpaRepository;

    @Override
    public UserSession save(UserSession session) {
        return userSessionJpaRepository.save(session);
    }

    @Override
    public Optional<UserSession> findByToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        return userSessionJpaRepository.findByToken(token.trim()); // .trim() elimina espacios al inicio y final
    }
}

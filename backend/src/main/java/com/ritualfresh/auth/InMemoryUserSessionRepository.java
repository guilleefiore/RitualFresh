package com.ritualfresh.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryUserSessionRepository implements UserSessionRepository {
    private final Map<String, UserSession> sessionsByToken = new HashMap<>();
    private final AtomicLong sequenceIds = new AtomicLong(1);

    @Override
    public UserSession save(UserSession session) {
        session.assignIdIfMissing(sequenceIds.getAndIncrement());
        sessionsByToken.put(session.getToken(), session);
        return session;
    }

    @Override
    public Optional<UserSession> findByToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        return Optional.ofNullable(sessionsByToken.get(token.trim()));
    }
}

package com.ritualfresh.auth.repository;

import com.ritualfresh.auth.model.UserSession;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryUserSessionRepository implements UserSessionRepository {
    private final Map<String, UserSession> sessionsByToken = new HashMap<>();
    private final AtomicLong sequenceIds = new AtomicLong(1);

    @Override
    public UserSession save(UserSession session) {
        assignIdIfMissing(session, sequenceIds.getAndIncrement());
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

    private void assignIdIfMissing(UserSession session, long id) {
        if (session.getId() != null) {
            return;
        }

        try {
            Field field = UserSession.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(session, id);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("No se pudo asignar el identificador de la sesion en memoria.", exception);
        }
    }
}

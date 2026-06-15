package com.ritualfresh.auth.repository;

import com.ritualfresh.auth.model.UserSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

// Permite probar login y autenticacion sin usar base de datos real.
public class InMemoryUserSessionRepository implements UserSessionRepository {
    // Guarda cada sesion usando el token como clave de acceso.
    private final Map<String, UserSession> sessionsByToken = new HashMap<>();
    // Simula ids autogenerados para las sesiones creadas en tests.
    private final AtomicLong sequenceIds = new AtomicLong(1);

    @Override
    // Guarda la sesion en memoria y le asigna id si todavia no tiene.
    public UserSession save(UserSession session) {
        session.assignIdIfMissing(sequenceIds.getAndIncrement());
        sessionsByToken.put(session.getToken(), session);
        return session;
    }

    @Override
    // Busca una sesion por token y evita consultas con valores vacios.
    public Optional<UserSession> findByToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        return Optional.ofNullable(sessionsByToken.get(token.trim()));
    }
}

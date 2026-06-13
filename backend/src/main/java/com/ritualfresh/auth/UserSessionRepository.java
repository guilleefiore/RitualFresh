package com.ritualfresh.auth;

import java.util.Optional;

public interface UserSessionRepository {
    UserSession save(UserSession session);

    Optional<UserSession> findByToken(String token);
}

package com.ritualfresh.auth.repository;

import com.ritualfresh.auth.model.UserSession;

import java.util.Optional;

public interface UserSessionRepository {
    UserSession save(UserSession session);

    Optional<UserSession> findByToken(String token);
}

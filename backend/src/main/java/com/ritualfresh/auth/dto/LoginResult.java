package com.ritualfresh.auth.dto;

import com.ritualfresh.auth.model.User;

import java.time.LocalDateTime;

public record LoginResult(
        User user,
        String sessionToken,
        LocalDateTime sessionExpiresAt,
        boolean isNewUser) {

    public LoginResult(User user, String sessionToken, LocalDateTime sessionExpiresAt) {
        this(user, sessionToken, sessionExpiresAt, false);
    }
}

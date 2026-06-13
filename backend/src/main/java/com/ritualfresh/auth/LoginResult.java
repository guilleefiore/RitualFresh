package com.ritualfresh.auth;

import java.time.LocalDateTime;

public record LoginResult(
        User user,
        String sessionToken,
        LocalDateTime sessionExpiresAt) {
}

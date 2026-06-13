package com.ritualfresh.auth.controller;

import java.time.LocalDateTime;

public record LoginApiResponse(
        String message,
        String sessionToken,
        LocalDateTime sessionExpiresAt,
        UserApiResponse user) {
}

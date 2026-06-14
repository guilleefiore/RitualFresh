package com.ritualfresh.auth.dto;

import java.time.LocalDateTime;

public record LoginApiResponse(
        String message,
        String sessionToken,
        LocalDateTime sessionExpiresAt,
        UserApiResponse user) {
}

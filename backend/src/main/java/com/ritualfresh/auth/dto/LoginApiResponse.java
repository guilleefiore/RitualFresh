package com.ritualfresh.auth.dto;

import java.time.LocalDateTime;

public record LoginApiResponse(
        String message,
        LocalDateTime sessionExpiresAt,
        UserApiResponse user) {
}

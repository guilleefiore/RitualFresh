package com.ritualfresh.auth.dto;

import java.time.LocalDateTime;

public record PasswordResetApiResponse(
        String message,
        LocalDateTime expiresAt) {
}

package com.ritualfresh.auth.dto;

import java.time.LocalDateTime;

public record PasswordResetResult(
        String message,
        String resetToken,
        LocalDateTime expiresAt) {
}

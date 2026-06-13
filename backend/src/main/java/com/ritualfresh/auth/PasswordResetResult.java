package com.ritualfresh.auth;

import java.time.LocalDateTime;

public record PasswordResetResult(
        String message,
        String resetToken,
        LocalDateTime expiresAt) {
}

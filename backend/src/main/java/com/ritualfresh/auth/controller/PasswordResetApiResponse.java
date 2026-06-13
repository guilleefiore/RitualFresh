package com.ritualfresh.auth.controller;

import java.time.LocalDateTime;

public record PasswordResetApiResponse(
        String message,
        String resetToken,
        LocalDateTime expiresAt) {
}

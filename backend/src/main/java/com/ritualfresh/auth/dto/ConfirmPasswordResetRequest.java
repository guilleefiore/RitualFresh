package com.ritualfresh.auth.dto;

public record ConfirmPasswordResetRequest(
        String resetToken,
        String password,
        String confirmPassword) {
}

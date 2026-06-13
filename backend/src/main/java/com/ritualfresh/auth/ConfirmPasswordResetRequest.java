package com.ritualfresh.auth;

public record ConfirmPasswordResetRequest(
        String resetToken,
        String password,
        String confirmPassword) {
}

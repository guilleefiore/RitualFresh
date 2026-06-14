package com.ritualfresh.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record ConfirmPasswordResetApiRequest(
        @NotBlank String resetToken,
        @NotBlank String password,
        @NotBlank String confirmPassword) {
}

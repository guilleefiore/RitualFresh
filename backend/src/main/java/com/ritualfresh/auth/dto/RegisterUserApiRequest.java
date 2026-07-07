package com.ritualfresh.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterUserApiRequest(
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotBlank String confirmPassword,
        @NotNull RegisterUserRole role) {
}

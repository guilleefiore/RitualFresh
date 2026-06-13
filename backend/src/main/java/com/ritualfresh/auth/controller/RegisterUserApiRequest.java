package com.ritualfresh.auth.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterUserApiRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String documentNumber,
        @NotBlank String phoneNumber,
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotBlank String confirmPassword,
        @NotNull RegisterUserRole role) {
}

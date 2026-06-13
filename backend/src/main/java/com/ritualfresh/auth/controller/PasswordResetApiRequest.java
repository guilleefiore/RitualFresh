package com.ritualfresh.auth.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordResetApiRequest(
        @NotBlank @Email String email) {
}

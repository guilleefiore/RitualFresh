package com.ritualfresh.auth.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginApiRequest(
        @NotBlank @Email String email,
        @NotBlank String password) {
}

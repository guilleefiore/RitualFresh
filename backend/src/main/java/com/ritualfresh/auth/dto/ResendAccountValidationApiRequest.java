package com.ritualfresh.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResendAccountValidationApiRequest(
        @NotBlank @Email String email) {
}

package com.ritualfresh.auth.dto;

public record RegisterUserApiResponse(
        String message,
        String accountValidationToken,
        UserApiResponse user) {
}

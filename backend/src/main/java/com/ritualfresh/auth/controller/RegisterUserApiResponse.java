package com.ritualfresh.auth.controller;

public record RegisterUserApiResponse(
        String message,
        String accountValidationToken,
        UserApiResponse user) {
}

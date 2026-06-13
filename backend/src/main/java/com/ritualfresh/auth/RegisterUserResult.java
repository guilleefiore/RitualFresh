package com.ritualfresh.auth;

public record RegisterUserResult(
        User user,
        String message,
        String accountValidationToken) {
}

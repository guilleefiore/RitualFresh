package com.ritualfresh.auth;

public record RegisterUserRequest(
        String firstName,
        String lastName,
        String documentNumber,
        String phoneNumber,
        String email,
        String password,
        String confirmPassword,
        UserRole role) {
}

package com.ritualfresh.auth.dto;

import com.ritualfresh.auth.model.UserRole;

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

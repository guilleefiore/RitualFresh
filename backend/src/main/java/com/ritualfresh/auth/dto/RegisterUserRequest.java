package com.ritualfresh.auth.dto;

import com.ritualfresh.auth.model.UserRole;

public record RegisterUserRequest(
        String email,
        String password,
        String confirmPassword,
        UserRole role) {
}

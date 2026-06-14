package com.ritualfresh.auth.dto;

import com.ritualfresh.auth.model.User;

public record RegisterUserResult(
        User user,
        String message,
        String accountValidationToken) {
}

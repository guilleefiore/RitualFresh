package com.ritualfresh.admin.dto;

import com.ritualfresh.auth.model.AccountStatus;
import com.ritualfresh.auth.model.User;
import com.ritualfresh.auth.model.UserRole;

import java.time.LocalDateTime;

public record AdminUserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        UserRole role,
        AccountStatus accountStatus,
        LocalDateTime createdAt,
        LocalDateTime deactivatedAt) {

    public static AdminUserResponse from(User user) {
        return new AdminUserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.getAccountStatus(),
                user.getCreatedAt(),
                user.getDeactivatedAt());
    }
}

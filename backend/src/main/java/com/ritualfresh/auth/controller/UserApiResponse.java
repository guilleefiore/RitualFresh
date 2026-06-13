package com.ritualfresh.auth.controller;

import com.ritualfresh.auth.AccountStatus;
import com.ritualfresh.auth.User;
import com.ritualfresh.auth.UserRole;

public record UserApiResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        UserRole role,
        AccountStatus accountStatus) {

    public static UserApiResponse from(User user) {
        return new UserApiResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.getAccountStatus());
    }
}

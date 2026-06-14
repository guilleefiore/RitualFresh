package com.ritualfresh.auth.dto;

import com.ritualfresh.auth.model.AccountStatus;
import com.ritualfresh.auth.model.User;
import com.ritualfresh.auth.model.UserRole;

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

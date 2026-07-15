package com.ritualfresh.admin.dto;

import com.ritualfresh.auth.model.AccountStatus;
import com.ritualfresh.auth.model.User;
import com.ritualfresh.auth.model.UserRole;

import java.time.LocalDateTime;
import java.util.List;

public record AdminUserDetailResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String documentNumber,
        UserRole role,
        AccountStatus accountStatus,
        LocalDateTime createdAt,
        LocalDateTime deactivatedAt,
        List<AdminAccountStatus> allowedStatusTransitions) {

    public static AdminUserDetailResponse from(User user, List<AdminAccountStatus> allowedStatusTransitions) {
        return new AdminUserDetailResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getDocumentNumber(),
                user.getRole(),
                user.getAccountStatus(),
                user.getCreatedAt(),
                user.getDeactivatedAt(),
                allowedStatusTransitions);
    }
}

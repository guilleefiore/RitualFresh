package com.ritualfresh.auth.dto;

import com.ritualfresh.auth.model.UserRole;

public enum RegisterUserRole {
    CLIENT,
    WORKER;

    public UserRole toUserRole() {
        return UserRole.valueOf(name());
    }
}

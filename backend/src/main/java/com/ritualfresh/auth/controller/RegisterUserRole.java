package com.ritualfresh.auth.controller;

import com.ritualfresh.auth.UserRole;

public enum RegisterUserRole {
    CLIENT,
    WORKER;

    public UserRole toUserRole() {
        return UserRole.valueOf(name());
    }
}

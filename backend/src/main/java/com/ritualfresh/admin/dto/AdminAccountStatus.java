package com.ritualfresh.admin.dto;

import com.ritualfresh.auth.model.AccountStatus;

public enum AdminAccountStatus {
    ACTIVE,
    SUSPENDED,
    DELETED;

    public AccountStatus toAccountStatus() {
        return AccountStatus.valueOf(name());
    }
}

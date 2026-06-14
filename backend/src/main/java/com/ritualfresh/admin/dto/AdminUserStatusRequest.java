package com.ritualfresh.admin.dto;

import jakarta.validation.constraints.NotNull;

public record AdminUserStatusRequest(
        @NotNull AdminAccountStatus status) {
}

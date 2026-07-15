package com.ritualfresh.admin.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminUserStatusRequest(
        @NotNull AdminAccountStatus status,
        @NotBlank @Size(max = 500) String reason) {
}

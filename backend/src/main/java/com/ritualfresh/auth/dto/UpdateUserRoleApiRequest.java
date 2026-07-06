package com.ritualfresh.auth.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateUserRoleApiRequest(
        @NotNull RegisterUserRole role) {
}

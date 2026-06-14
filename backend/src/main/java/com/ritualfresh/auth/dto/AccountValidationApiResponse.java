package com.ritualfresh.auth.dto;

public record AccountValidationApiResponse(
        String message,
        UserApiResponse user) {
}

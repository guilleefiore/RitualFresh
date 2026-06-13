package com.ritualfresh.auth.controller;

public record AccountValidationApiResponse(
        String message,
        UserApiResponse user) {
}

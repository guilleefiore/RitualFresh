package com.ritualfresh.auth.dto;

public record OAuth2ProfileData(
        String email,
        String firstName,
        String lastName) {
}

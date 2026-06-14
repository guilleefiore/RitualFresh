package com.ritualfresh.profiles.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateClientProfileApiRequest(
        String photoUrl,
        @NotBlank String contactPhone,
        @NotBlank String streetName,
        @NotBlank String streetNumber,
        String floor,
        String apartment,
        @NotBlank String postalCode,
        @NotBlank String city,
        @NotBlank String province,
        @NotBlank String hiringPreferences) {
}

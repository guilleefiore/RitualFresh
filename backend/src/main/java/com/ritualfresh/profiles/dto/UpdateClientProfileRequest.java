package com.ritualfresh.profiles.dto;

public record UpdateClientProfileRequest(
        String photoUrl,
        String contactPhone,
        String streetName,
        String streetNumber,
        String floor,
        String apartment,
        String postalCode,
        String city,
        String province,
        String hiringPreferences) {
}

package com.ritualfresh.profiles;

public record CreateClientProfileRequest(
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

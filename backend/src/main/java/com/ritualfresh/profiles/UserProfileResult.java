package com.ritualfresh.profiles;

import com.ritualfresh.auth.User;

import java.math.BigDecimal;

public record UserProfileResult(
        ProfileType profileType,
        Long profileId,
        Long userId,
        String firstName,
        String lastName,
        String email,
        String photoUrl,
        Integer clientRating,
        Integer rankingPosition,
        String description,
        Integer yearsOfExperience,
        String offeredServices,
        String workArea,
        String availability,
        BigDecimal hourlyRate,
        String contactPhone,
        String streetName,
        String streetNumber,
        String floor,
        String apartment,
        String postalCode,
        String city,
        String province,
        String hiringPreferences) {

    public static UserProfileResult from(ClientProfile profile) {
        User user = profile.getUser();

        return new UserProfileResult(
                ProfileType.CLIENT,
                profile.getId(),
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                profile.getPhotoUrl(),
                profile.getClientRating(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                profile.getContactPhone(),
                profile.getStreetName(),
                profile.getStreetNumber(),
                profile.getFloor(),
                profile.getApartment(),
                profile.getPostalCode(),
                profile.getCity(),
                profile.getProvince(),
                profile.getHiringPreferences());
    }

    public static UserProfileResult from(WorkerProfile profile) {
        User user = profile.getUser();

        return new UserProfileResult(
                ProfileType.WORKER,
                profile.getId(),
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                profile.getPhotoUrl(),
                null,
                profile.getRankingPosition(),
                profile.getDescription(),
                profile.getYearsOfExperience(),
                profile.getOfferedServices(),
                profile.getWorkArea(),
                profile.getAvailability(),
                profile.getHourlyRate(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
    }
}

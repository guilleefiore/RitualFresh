package com.ritualfresh.profiles.controller;

import com.ritualfresh.profiles.ProfileType;
import com.ritualfresh.profiles.UserProfileResult;

import java.math.BigDecimal;

public record ProfileApiResponse(
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

    public static ProfileApiResponse from(UserProfileResult result) {
        return new ProfileApiResponse(
                result.profileType(),
                result.profileId(),
                result.userId(),
                result.firstName(),
                result.lastName(),
                result.email(),
                result.photoUrl(),
                result.clientRating(),
                result.rankingPosition(),
                result.description(),
                result.yearsOfExperience(),
                result.offeredServices(),
                result.workArea(),
                result.availability(),
                result.hourlyRate(),
                result.contactPhone(),
                result.streetName(),
                result.streetNumber(),
                result.floor(),
                result.apartment(),
                result.postalCode(),
                result.city(),
                result.province(),
                result.hiringPreferences());
    }
}

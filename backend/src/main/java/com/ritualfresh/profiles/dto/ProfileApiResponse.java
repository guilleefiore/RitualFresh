package com.ritualfresh.profiles.dto;

import com.ritualfresh.profiles.model.ProfileType;
import com.ritualfresh.profiles.model.PreferredTimeSlot;
import com.ritualfresh.profiles.model.ServiceFrequency;
import com.ritualfresh.profiles.model.ServiceInterest;

import java.math.BigDecimal;
import java.util.Set;

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
        ServiceFrequency serviceFrequency,
        Set<PreferredTimeSlot> preferredTimeSlots,
        Set<ServiceInterest> serviceInterests,
        String otherServiceInterest,
        String additionalNotes) {

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
                result.serviceFrequency(),
                result.preferredTimeSlots(),
                result.serviceInterests(),
                result.otherServiceInterest(),
                result.additionalNotes());
    }
}

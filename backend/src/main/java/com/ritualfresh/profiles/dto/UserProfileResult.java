package com.ritualfresh.profiles.dto;

import com.ritualfresh.auth.model.User;
import com.ritualfresh.profiles.model.ClientProfile;
import com.ritualfresh.profiles.model.PreferredTimeSlot;
import com.ritualfresh.profiles.model.ProfileType;
import com.ritualfresh.profiles.model.ServiceFrequency;
import com.ritualfresh.profiles.model.ServiceInterest;
import com.ritualfresh.profiles.model.WorkerProfile;

import java.math.BigDecimal;
import java.util.Set;

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
        ServiceFrequency serviceFrequency,
        Set<PreferredTimeSlot> preferredTimeSlots,
        Set<ServiceInterest> serviceInterests,
        String otherServiceInterest,
        String additionalNotes) {

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
                profile.getServiceFrequency(),
                Set.copyOf(profile.getPreferredTimeSlots()),
                Set.copyOf(profile.getServiceInterests()),
                profile.getOtherServiceInterest(),
                profile.getAdditionalNotes());
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
                user.getPhoneNumber(),
                null,
                null,
                null,
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

package com.ritualfresh.profiles.dto;

import com.ritualfresh.profiles.model.PreferredTimeSlot;
import com.ritualfresh.profiles.model.ServiceFrequency;
import com.ritualfresh.profiles.model.ServiceInterest;

import java.util.Set;

public record UpdateClientProfileRequest(
        String firstName,
        String lastName,
        String photoUrl,
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
}

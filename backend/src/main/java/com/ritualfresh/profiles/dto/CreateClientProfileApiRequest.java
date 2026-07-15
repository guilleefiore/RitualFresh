package com.ritualfresh.profiles.dto;

import com.ritualfresh.profiles.model.PreferredTimeSlot;
import com.ritualfresh.profiles.model.ServiceFrequency;
import com.ritualfresh.profiles.model.ServiceInterest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CreateClientProfileApiRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String photoUrl,
        @NotBlank String contactPhone,
        @NotBlank String streetName,
        @NotBlank String streetNumber,
        String floor,
        String apartment,
        @NotBlank String postalCode,
        @NotBlank String city,
        @NotBlank String province,
        @NotNull ServiceFrequency serviceFrequency,
        @NotEmpty Set<PreferredTimeSlot> preferredTimeSlots,
        @NotEmpty Set<ServiceInterest> serviceInterests,
        @Size(max = 120) String otherServiceInterest,
        @Size(max = 500) String additionalNotes) {
}

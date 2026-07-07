package com.ritualfresh.profiles.dto;

import java.math.BigDecimal;

public record UpdateWorkerProfileRequest(
        String firstName,
        String lastName,
        String photoUrl,
        String contactPhone,
        String description,
        Integer yearsOfExperience,
        String offeredServices,
        String workArea,
        String availability,
        BigDecimal hourlyRate) {
}

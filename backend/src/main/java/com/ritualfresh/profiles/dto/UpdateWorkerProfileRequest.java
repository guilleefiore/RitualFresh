package com.ritualfresh.profiles.dto;

import java.math.BigDecimal;

public record UpdateWorkerProfileRequest(
        String photoUrl,
        String description,
        Integer yearsOfExperience,
        String offeredServices,
        String workArea,
        String availability,
        BigDecimal hourlyRate) {
}

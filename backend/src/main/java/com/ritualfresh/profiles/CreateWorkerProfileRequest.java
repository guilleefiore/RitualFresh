package com.ritualfresh.profiles;

import java.math.BigDecimal;

public record CreateWorkerProfileRequest(
        String photoUrl,
        String description,
        Integer yearsOfExperience,
        String offeredServices,
        String workArea,
        String availability,
        BigDecimal hourlyRate) {
}

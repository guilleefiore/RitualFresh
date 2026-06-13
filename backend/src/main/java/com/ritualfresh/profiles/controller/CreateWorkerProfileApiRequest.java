package com.ritualfresh.profiles.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateWorkerProfileApiRequest(
        String photoUrl,
        @NotBlank String description,
        @NotNull @Min(0) Integer yearsOfExperience,
        @NotBlank String offeredServices,
        @NotBlank String workArea,
        @NotBlank String availability,
        @NotNull @Positive BigDecimal hourlyRate) {
}

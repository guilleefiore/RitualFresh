package com.ritualfresh.profiles.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateWorkerProfileApiRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String photoUrl,
        @NotBlank String contactPhone,
        @NotBlank String description,
        @NotNull @Min(0) Integer yearsOfExperience,
        @NotBlank String offeredServices,
        @NotBlank String workArea,
        @NotBlank String availability,
        @NotNull @Positive BigDecimal hourlyRate) {
}

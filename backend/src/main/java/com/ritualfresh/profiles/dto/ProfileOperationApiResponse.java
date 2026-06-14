package com.ritualfresh.profiles.dto;

public record ProfileOperationApiResponse(
        String message,
        ProfileApiResponse profile) {
}

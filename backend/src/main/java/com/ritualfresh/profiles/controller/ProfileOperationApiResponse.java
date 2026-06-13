package com.ritualfresh.profiles.controller;

public record ProfileOperationApiResponse(
        String message,
        ProfileApiResponse profile) {
}

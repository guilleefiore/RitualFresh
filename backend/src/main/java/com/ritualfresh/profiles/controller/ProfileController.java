package com.ritualfresh.profiles.controller;

import com.ritualfresh.profiles.dto.CreateClientProfileApiRequest;
import com.ritualfresh.profiles.dto.CreateClientProfileRequest;
import com.ritualfresh.profiles.dto.CreateWorkerProfileApiRequest;
import com.ritualfresh.profiles.dto.CreateWorkerProfileRequest;
import com.ritualfresh.profiles.dto.ProfileApiResponse;
import com.ritualfresh.profiles.dto.ProfileOperationApiResponse;
import com.ritualfresh.profiles.dto.UpdateClientProfileApiRequest;
import com.ritualfresh.profiles.dto.UpdateClientProfileRequest;
import com.ritualfresh.profiles.dto.UpdateWorkerProfileApiRequest;
import com.ritualfresh.profiles.dto.UpdateWorkerProfileRequest;
import com.ritualfresh.profiles.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @PostMapping("/clientes")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileOperationApiResponse createClientProfile(
            Authentication authentication,
            @Valid @RequestBody CreateClientProfileApiRequest request) {
        extractSessionToken(authentication);
        ProfileApiResponse profile = ProfileApiResponse.from(profileService.createClientProfile(new CreateClientProfileRequest(
                request.photoUrl(),
                request.contactPhone(),
                request.streetName(),
                request.streetNumber(),
                request.floor(),
                request.apartment(),
                request.postalCode(),
                request.city(),
                request.province(),
                request.hiringPreferences())));

        return new ProfileOperationApiResponse("Profile de cliente creado correctamente.", profile);
    }

    @PostMapping("/trabajadores")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileOperationApiResponse createWorkerProfile(
            Authentication authentication,
            @Valid @RequestBody CreateWorkerProfileApiRequest request) {
        extractSessionToken(authentication);
        ProfileApiResponse profile = ProfileApiResponse.from(profileService.createWorkerProfile(new CreateWorkerProfileRequest(
                request.photoUrl(),
                request.description(),
                request.yearsOfExperience(),
                request.offeredServices(),
                request.workArea(),
                request.availability(),
                request.hourlyRate())));

        return new ProfileOperationApiResponse("Profile de trabajador creado correctamente.", profile);
    }

    @GetMapping("/me")
    public ProfileApiResponse getMyProfile(Authentication authentication) {
        extractSessionToken(authentication);
        return ProfileApiResponse.from(profileService.getMyProfile());
    }

    @PutMapping("/clientes/me")
    public ProfileOperationApiResponse updateClientProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateClientProfileApiRequest request) {
        extractSessionToken(authentication);
        ProfileApiResponse profile = ProfileApiResponse.from(profileService.updateClientProfile(new UpdateClientProfileRequest(
                request.photoUrl(),
                request.contactPhone(),
                request.streetName(),
                request.streetNumber(),
                request.floor(),
                request.apartment(),
                request.postalCode(),
                request.city(),
                request.province(),
                request.hiringPreferences())));

        return new ProfileOperationApiResponse("Profile de cliente actualizado correctamente.", profile);
    }

    @PutMapping("/trabajadores/me")
    public ProfileOperationApiResponse updateWorkerProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateWorkerProfileApiRequest request) {
        extractSessionToken(authentication);
        ProfileApiResponse profile = ProfileApiResponse.from(profileService.updateWorkerProfile(new UpdateWorkerProfileRequest(
                request.photoUrl(),
                request.description(),
                request.yearsOfExperience(),
                request.offeredServices(),
                request.workArea(),
                request.availability(),
                request.hourlyRate())));

        return new ProfileOperationApiResponse("Profile de trabajador actualizado correctamente.", profile);
    }

    private String extractSessionToken(Authentication authentication) {
        return authentication == null || authentication.getCredentials() == null
                ? null
                : authentication.getCredentials().toString();
    }
}

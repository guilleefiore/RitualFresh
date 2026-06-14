package com.ritualfresh.profiles.controller;

import com.ritualfresh.profiles.CreateClientProfileRequest;
import com.ritualfresh.profiles.CreateWorkerProfileRequest;
import com.ritualfresh.profiles.ProfileService;
import com.ritualfresh.profiles.UpdateClientProfileRequest;
import com.ritualfresh.profiles.UpdateWorkerProfileRequest;
import com.ritualfresh.shared.exception.BusinessRuleException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping("/clientes")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileOperationApiResponse createClientProfile(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody CreateClientProfileApiRequest request) {
        ProfileApiResponse profile = ProfileApiResponse.from(profileService.createClientProfile(
                extractSessionToken(authorization),
                new CreateClientProfileRequest(
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
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody CreateWorkerProfileApiRequest request) {
        ProfileApiResponse profile = ProfileApiResponse.from(profileService.createWorkerProfile(
                extractSessionToken(authorization),
                new CreateWorkerProfileRequest(
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
    public ProfileApiResponse getMyProfile(@RequestHeader("Authorization") String authorization) {
        return ProfileApiResponse.from(profileService.getMyProfile(extractSessionToken(authorization)));
    }

    @PutMapping("/clientes/me")
    public ProfileOperationApiResponse updateClientProfile(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody UpdateClientProfileApiRequest request) {
        ProfileApiResponse profile = ProfileApiResponse.from(profileService.updateClientProfile(
                extractSessionToken(authorization),
                new UpdateClientProfileRequest(
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
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody UpdateWorkerProfileApiRequest request) {
        ProfileApiResponse profile = ProfileApiResponse.from(profileService.updateWorkerProfile(
                extractSessionToken(authorization),
                new UpdateWorkerProfileRequest(
                        request.photoUrl(),
                        request.description(),
                        request.yearsOfExperience(),
                        request.offeredServices(),
                        request.workArea(),
                        request.availability(),
                        request.hourlyRate())));

        return new ProfileOperationApiResponse("Profile de trabajador actualizado correctamente.", profile);
    }

    private String extractSessionToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new BusinessRuleException("Debe iniciar sesion para acceder a esta funcionalidad.");
        }

        return authorization.substring("Bearer ".length()).trim();
    }
}

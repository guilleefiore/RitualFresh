package com.ritualfresh.profiles.service;

import com.ritualfresh.auth.model.User;
import com.ritualfresh.auth.model.UserRole;
import com.ritualfresh.auth.service.UserService;
import com.ritualfresh.profiles.dto.CreateClientProfileRequest;
import com.ritualfresh.profiles.dto.CreateWorkerProfileRequest;
import com.ritualfresh.profiles.dto.UpdateClientProfileRequest;
import com.ritualfresh.profiles.dto.UpdateWorkerProfileRequest;
import com.ritualfresh.profiles.dto.UserProfileResult;
import com.ritualfresh.profiles.model.ClientProfile;
import com.ritualfresh.profiles.model.PreferredTimeSlot;
import com.ritualfresh.profiles.model.ServiceFrequency;
import com.ritualfresh.profiles.model.ServiceInterest;
import com.ritualfresh.profiles.model.WorkerProfile;
import com.ritualfresh.profiles.repository.ClientProfileRepository;
import com.ritualfresh.profiles.repository.WorkerProfileRepository;
import com.ritualfresh.shared.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9+()\\-\\s]{7,30}$");
    private static final Pattern STREET_PATTERN = Pattern.compile("^[\\p{L}0-9 .'-]{2,120}$");
    private static final Pattern STREET_NUMBER_PATTERN = Pattern.compile("^[0-9]{1,6}[A-Za-z]?$");
    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^[A-Za-z0-9\\s-]{3,12}$");
    private static final Pattern AVAILABILITY_PATTERN = Pattern.compile("^.+ de ([01]\\d|2[0-3]):[0-5]\\d a ([01]\\d|2[0-3]):[0-5]\\d$");
    private static final int MIN_WORKER_DESCRIPTION_LENGTH = 30;
    private static final int MAX_OTHER_SERVICE_LENGTH = 120;
    private static final int MAX_ADDITIONAL_NOTES_LENGTH = 500;

    private final UserService userService;
    private final ClientProfileRepository clientProfileRepository;
    private final WorkerProfileRepository workerProfileRepository;

    // Crea un perfil de cliente para el usuario autenticado.
    @PreAuthorize("hasRole('CLIENT')")
    @Transactional
    public UserProfileResult createClientProfile(CreateClientProfileRequest request) {
        validateClientRequest(request);
        User user = userService.getAuthenticatedUser();
        validateRole(user, UserRole.CLIENT, "El rol del usuario no permite crear un perfil de cliente.");
        validateUserWithoutProfile(user.getId());
        userService.updateAuthenticatedUserProfileData(
                request.firstName(),
                request.lastName(),
                validatePhoneNumber(request.contactPhone()));
        Set<ServiceInterest> serviceInterests = validateServiceInterests(request.serviceInterests());

        ClientProfile profile = new ClientProfile(
                user,
                validatePhotoUrl(request.photoUrl()),
                validatePhoneNumber(request.contactPhone()),
                validateStreetName(request.streetName()),
                validateStreetNumber(request.streetNumber()),
                normalizeOptional(request.floor()),
                normalizeOptional(request.apartment()),
                validatePostalCode(request.postalCode()),
                validateText(request.city(), "localidad"),
                validateText(request.province(), "provincia"),
                validateServiceFrequency(request.serviceFrequency()),
                validatePreferredTimeSlots(request.preferredTimeSlots()),
                serviceInterests,
                validateOtherServiceInterest(serviceInterests, request.otherServiceInterest()),
                normalizeOptional(request.additionalNotes(), MAX_ADDITIONAL_NOTES_LENGTH, "observaciones adicionales"));

        return UserProfileResult.from(clientProfileRepository.save(profile));
    }

    // Crea un perfil de trabajador para el usuario autenticado.
    @PreAuthorize("hasRole('WORKER')")
    @Transactional
    public UserProfileResult createWorkerProfile(CreateWorkerProfileRequest request) {
        validateWorkerRequest(request);
        User user = userService.getAuthenticatedUser();
        validateRole(user, UserRole.WORKER, "El rol del usuario no permite crear un perfil de trabajador.");
        validateUserWithoutProfile(user.getId());
        userService.updateAuthenticatedUserProfileData(
                request.firstName(),
                request.lastName(),
                validatePhoneNumber(request.contactPhone()));

        WorkerProfile profile = new WorkerProfile(
                user,
                validatePhotoUrl(request.photoUrl()),
                validateWorkerDescription(request.description()),
                validateYearsOfExperience(request.yearsOfExperience()),
                validateText(request.offeredServices(), "servicios ofrecidos"),
                validateText(request.workArea(), "zona de trabajo"),
                validateAvailability(request.availability()),
                validateHourlyRate(request.hourlyRate()));

        return UserProfileResult.from(workerProfileRepository.save(profile));
    }

    // Devuelve el perfil asociado al usuario autenticado.
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public UserProfileResult getMyProfile() {
        User user = userService.getAuthenticatedUser();

        if (user.getRole() == UserRole.CLIENT) {
            return clientProfileRepository.findByUserId(user.getId())
                    .map(UserProfileResult::from)
                    .orElseThrow(() -> new BusinessRuleException("El usuario no posee un perfil de cliente."));
        }

        if (user.getRole() == UserRole.WORKER) {
            return workerProfileRepository.findByUserId(user.getId())
                    .map(UserProfileResult::from)
                    .orElseThrow(() -> new BusinessRuleException("El usuario no posee un perfil de trabajador."));
        }

        throw new BusinessRuleException("El rol del usuario no posee perfil asociado.");
    }

    // Actualiza los datos del perfil de cliente del usuario autenticado.
    @PreAuthorize("hasRole('CLIENT')")
    @Transactional
    public UserProfileResult updateClientProfile(UpdateClientProfileRequest request) {
        if (request == null) {
            throw new BusinessRuleException("Debe completar los datos del perfil.");
        }

        User user = userService.getAuthenticatedUser();
        validateRole(user, UserRole.CLIENT, "El rol del usuario no permite editar un perfil de cliente.");
        userService.updateAuthenticatedUserProfileData(
                request.firstName(),
                request.lastName(),
                validatePhoneNumber(request.contactPhone()));

        ClientProfile profile = clientProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessRuleException("El usuario no posee un perfil de cliente."));
        Set<ServiceInterest> serviceInterests = validateServiceInterests(request.serviceInterests());
        profile.edit(
                validatePhotoUrl(request.photoUrl()),
                validatePhoneNumber(request.contactPhone()),
                validateStreetName(request.streetName()),
                validateStreetNumber(request.streetNumber()),
                normalizeOptional(request.floor()),
                normalizeOptional(request.apartment()),
                validatePostalCode(request.postalCode()),
                validateText(request.city(), "localidad"),
                validateText(request.province(), "provincia"),
                validateServiceFrequency(request.serviceFrequency()),
                validatePreferredTimeSlots(request.preferredTimeSlots()),
                serviceInterests,
                validateOtherServiceInterest(serviceInterests, request.otherServiceInterest()),
                normalizeOptional(request.additionalNotes(), MAX_ADDITIONAL_NOTES_LENGTH, "observaciones adicionales"));

        return UserProfileResult.from(clientProfileRepository.save(profile));
    }

    // Actualiza los datos del perfil de trabajador del usuario autenticado.
    @PreAuthorize("hasRole('WORKER')")
    @Transactional
    public UserProfileResult updateWorkerProfile(UpdateWorkerProfileRequest request) {
        if (request == null) {
            throw new BusinessRuleException("Debe completar los datos del perfil.");
        }

        User user = userService.getAuthenticatedUser();
        validateRole(user, UserRole.WORKER, "El rol del usuario no permite editar un perfil de trabajador.");
        userService.updateAuthenticatedUserProfileData(
                request.firstName(),
                request.lastName(),
                validatePhoneNumber(request.contactPhone()));

        WorkerProfile profile = workerProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessRuleException("El usuario no posee un perfil de trabajador."));
        profile.edit(
                validatePhotoUrl(request.photoUrl()),
                validateWorkerDescription(request.description()),
                validateYearsOfExperience(request.yearsOfExperience()),
                validateText(request.offeredServices(), "servicios ofrecidos"),
                validateText(request.workArea(), "zona de trabajo"),
                validateAvailability(request.availability()),
                validateHourlyRate(request.hourlyRate()));

        return UserProfileResult.from(workerProfileRepository.save(profile));
    }

    private void validateClientRequest(CreateClientProfileRequest request) {
        if (request == null) {
            throw new BusinessRuleException("Debe completar los datos del perfil.");
        }
    }

    private void validateWorkerRequest(CreateWorkerProfileRequest request) {
        if (request == null) {
            throw new BusinessRuleException("Debe completar los datos del perfil.");
        }

        validatePhoneNumber(request.contactPhone());
    }

    // Verifica que el usuario tenga el rol esperado para la operación.
    private void validateRole(User user, UserRole expectedRole, String message) {
        if (user.getRole() != expectedRole) {
            throw new BusinessRuleException(message);
        }
    }

    // Evita que un usuario cree más de un perfil.
    private void validateUserWithoutProfile(Long userId) {
        if (clientProfileRepository.existsByUserId(userId)
                || workerProfileRepository.existsByUserId(userId)) {
            throw new BusinessRuleException("El usuario ya posee un perfil creado.");
        }
    }

    private String validatePhoneNumber(String phoneNumber) {
        String value = validateText(phoneNumber, "telefono de contacto");
        if (!PHONE_PATTERN.matcher(value).matches()) {
            throw new BusinessRuleException("El telefono de contacto no posee un formato valido.");
        }

        return value;
    }

    private String validateStreetName(String streetName) {
        String value = validateText(streetName, "calle");
        if (!STREET_PATTERN.matcher(value).matches()) {
            throw new BusinessRuleException("La direccion ingresada no posee un formato valido.");
        }

        return value;
    }

    private String validateStreetNumber(String streetNumber) {
        String value = validateText(streetNumber, "numero de domicilio");
        if (!STREET_NUMBER_PATTERN.matcher(value).matches()) {
            throw new BusinessRuleException("La direccion ingresada no posee un formato valido.");
        }

        return value;
    }

    private String validatePostalCode(String postalCode) {
        String value = validateText(postalCode, "codigo postal");
        if (!POSTAL_CODE_PATTERN.matcher(value).matches()) {
            throw new BusinessRuleException("La direccion ingresada no posee un formato valido.");
        }

        return value;
    }

    private ServiceFrequency validateServiceFrequency(ServiceFrequency frequency) {
        if (frequency == null) {
            throw new BusinessRuleException("Debe seleccionar la frecuencia del servicio.");
        }

        return frequency;
    }

    private Set<PreferredTimeSlot> validatePreferredTimeSlots(Set<PreferredTimeSlot> timeSlots) {
        if (timeSlots == null || timeSlots.isEmpty() || timeSlots.stream().anyMatch(Objects::isNull)) {
            throw new BusinessRuleException("Debe seleccionar al menos un horario de preferencia.");
        }

        return new LinkedHashSet<>(timeSlots);
    }

    private Set<ServiceInterest> validateServiceInterests(Set<ServiceInterest> serviceInterests) {
        if (serviceInterests == null || serviceInterests.isEmpty() || serviceInterests.stream().anyMatch(Objects::isNull)) {
            throw new BusinessRuleException("Debe seleccionar al menos un servicio de interes.");
        }

        return new LinkedHashSet<>(serviceInterests);
    }

    private String validateOtherServiceInterest(Set<ServiceInterest> serviceInterests, String otherServiceInterest) {
        if (!serviceInterests.contains(ServiceInterest.OTHER)) {
            return null;
        }

        String value = validateText(otherServiceInterest, "otro servicio de interes");
        if (value.length() > MAX_OTHER_SERVICE_LENGTH) {
            throw new BusinessRuleException("El otro servicio de interes no debe superar los 120 caracteres.");
        }

        return value;
    }

    private int validateYearsOfExperience(Integer yearsOfExperience) {
        if (yearsOfExperience == null) {
            throw new BusinessRuleException("Debe completar los anios de experiencia.");
        }

        if (yearsOfExperience < 0) {
            throw new BusinessRuleException("Los anios de experiencia no pueden ser negativos.");
        }

        return yearsOfExperience;
    }

    private BigDecimal validateHourlyRate(BigDecimal hourlyRate) {
        if (hourlyRate == null) {
            throw new BusinessRuleException("Debe completar el precio por hora orientativo.");
        }

        if (hourlyRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("El precio por hora orientativo debe ser mayor a cero.");
        }

        return hourlyRate;
    }

    private String validateText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException("Debe completar el campo " + fieldName + ".");
        }

        return value.trim();
    }

    private String validatePhotoUrl(String photoUrl) {
        return validateText(photoUrl, "foto de perfil");
    }

    private String validateWorkerDescription(String description) {
        String value = validateText(description, "descripcion del trabajador");
        if (value.length() < MIN_WORKER_DESCRIPTION_LENGTH) {
            throw new BusinessRuleException("La descripcion del trabajador debe tener al menos 30 caracteres.");
        }

        return value;
    }

    private String validateAvailability(String availability) {
        String value = validateText(availability, "disponibilidad");
        if (!AVAILABILITY_PATTERN.matcher(value).matches()) {
            throw new BusinessRuleException("La disponibilidad debe indicar dias y horario en formato HH:mm.");
        }

        return value;
    }

    private String normalizeOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private String normalizeOptional(String value, int maxLength, String fieldName) {
        String normalized = normalizeOptional(value);
        if (normalized != null && normalized.length() > maxLength) {
            throw new BusinessRuleException("El campo " + fieldName + " supera el maximo permitido.");
        }

        return normalized;
    }
}

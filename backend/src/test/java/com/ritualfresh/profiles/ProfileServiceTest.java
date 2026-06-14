package com.ritualfresh.profiles;

import com.ritualfresh.auth.repository.InMemoryUserRepository;
import com.ritualfresh.auth.repository.InMemoryUserSessionRepository;
import com.ritualfresh.auth.dto.LoginRequest;
import com.ritualfresh.auth.dto.LoginResult;
import com.ritualfresh.auth.dto.RegisterUserRequest;
import com.ritualfresh.auth.dto.RegisterUserResult;
import com.ritualfresh.auth.model.User;
import com.ritualfresh.auth.repository.UserRepository;
import com.ritualfresh.auth.model.UserRole;
import com.ritualfresh.auth.service.UserService;
import com.ritualfresh.auth.repository.UserSessionRepository;
import com.ritualfresh.profiles.dto.CreateClientProfileRequest;
import com.ritualfresh.profiles.dto.CreateWorkerProfileRequest;
import com.ritualfresh.profiles.dto.UpdateWorkerProfileRequest;
import com.ritualfresh.profiles.dto.UserProfileResult;
import com.ritualfresh.profiles.model.ProfileType;
import com.ritualfresh.profiles.repository.InMemoryClientProfileRepository;
import com.ritualfresh.profiles.repository.InMemoryWorkerProfileRepository;
import com.ritualfresh.profiles.service.ProfileService;
import com.ritualfresh.shared.exception.BusinessRuleException;
import com.ritualfresh.shared.security.AuthenticatedUserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProfileServiceTest {
    private UserService userService;
    private ProfileService profileService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        UserRepository userRepository = new InMemoryUserRepository();
        UserSessionRepository userSessionRepository = new InMemoryUserSessionRepository();
        userService = new UserService(userRepository, userSessionRepository);
        profileService = new ProfileService(
                userService,
                new InMemoryClientProfileRepository(),
                new InMemoryWorkerProfileRepository());
    }

    @Test
    void us02M02Rf02CreatesClientProfileWithValidData() {
        LoginResult session = registerValidateAndLoginClient();
        authenticate(session);

        UserProfileResult result = profileService.createClientProfile(validClientRequest());

        assertEquals(ProfileType.CLIENT, result.profileType());
        assertEquals(session.user().getId(), result.userId());
        assertEquals("https://cdn.example.com/cliente.png", result.photoUrl());
        assertEquals("2615555555", result.contactPhone());
        assertEquals("San Martin", result.streetName());
        assertEquals("Limpieza semanal por la manana", result.hiringPreferences());
        assertEquals(0, result.clientRating());
    }

    @Test
    void us02M02Rf02PreventsClientPhoneWithInvalidFormat() {
        LoginResult session = registerValidateAndLoginClient();
        authenticate(session);
        CreateClientProfileRequest request = new CreateClientProfileRequest(
                null,
                "abc",
                "San Martin",
                "123",
                null,
                null,
                "5500",
                "Godoy Cruz",
                "Mendoza",
                "Limpieza semanal");

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> profileService.createClientProfile(request));

        assertEquals("El telefono de contacto no posee un formato valido.", exception.getMessage());
    }

    @Test
    void us02M02Rf02PreventsClientAddressWithInvalidFormat() {
        LoginResult session = registerValidateAndLoginClient();
        authenticate(session);
        CreateClientProfileRequest request = new CreateClientProfileRequest(
                null,
                "2615555555",
                "!",
                "123",
                null,
                null,
                "5500",
                "Godoy Cruz",
                "Mendoza",
                "Limpieza semanal");

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> profileService.createClientProfile(request));

        assertEquals("La direccion ingresada no posee un formato valido.", exception.getMessage());
    }

    @Test
    void us01M02Rf01CreatesEditsAndGetsWorkerProfileWithValidData() {
        LoginResult session = registerValidateAndLoginWorker();
        authenticate(session);
        profileService.createWorkerProfile(validWorkerRequest());

        UserProfileResult updated = profileService.updateWorkerProfile(new UpdateWorkerProfileRequest(
                "https://cdn.example.com/trabajador.png",
                "Limpieza general, profunda y mantenimiento preventivo",
                4,
                "Limpieza general, limpieza profunda, mantenimiento",
                "Gran Mendoza",
                "Lunes a viernes de 9 a 17",
                new BigDecimal("4500.00")));
        UserProfileResult obtained = profileService.getMyProfile();

        assertEquals(ProfileType.WORKER, updated.profileType());
        assertEquals("Limpieza general, profunda y mantenimiento preventivo", obtained.description());
        assertEquals(4, obtained.yearsOfExperience());
        assertEquals("Gran Mendoza", obtained.workArea());
        assertEquals(new BigDecimal("4500.00"), obtained.hourlyRate());
        assertEquals(0, obtained.rankingPosition());
    }

    @Test
    void us01M02Rf01PreventsNegativeExperienceYears() {
        LoginResult session = registerValidateAndLoginWorker();
        authenticate(session);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> profileService.createWorkerProfile(
                new CreateWorkerProfileRequest(
                        null,
                        "Limpieza profunda",
                        -1,
                        "Limpieza profunda",
                        "Mendoza",
                        "Turno tarde",
                        new BigDecimal("3500.00"))));

        assertEquals("Los anios de experiencia no pueden ser negativos.", exception.getMessage());
    }

    @Test
    void us01M02Rf01PreventsNonPositiveHourlyRate() {
        LoginResult session = registerValidateAndLoginWorker();
        authenticate(session);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> profileService.createWorkerProfile(
                new CreateWorkerProfileRequest(
                        null,
                        "Limpieza profunda",
                        2,
                        "Limpieza profunda",
                        "Mendoza",
                        "Turno tarde",
                        BigDecimal.ZERO)));

        assertEquals("El precio por hora orientativo debe ser mayor a cero.", exception.getMessage());
    }

    @Test
    void preventsCreatingClientProfileForWorkerUser() {
        LoginResult session = registerValidateAndLoginWorker();
        authenticate(session);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> profileService.createClientProfile(validClientRequest()));

        assertEquals("El rol del usuario no permite crear un perfil de cliente.", exception.getMessage());
    }

    @Test
    void preventsCreatingMoreThanOneProfilePerUser() {
        LoginResult session = registerValidateAndLoginClient();
        authenticate(session);
        profileService.createClientProfile(validClientRequest());

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> profileService.createClientProfile(validClientRequest()));

        assertEquals("El usuario ya posee un perfil creado.", exception.getMessage());
    }

    @Test
    void preventsManagingProfileWithoutSession() {
        SecurityContextHolder.clearContext();
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> profileService.createClientProfile(validClientRequest()));

        assertEquals("Debe iniciar sesion para acceder a esta funcionalidad.", exception.getMessage());
    }

    private LoginResult registerValidateAndLoginClient() {
        User user = registerAndValidateClient();
        return userService.login(new LoginRequest(user.getEmail(), "clave123"));
    }

    private LoginResult registerValidateAndLoginWorker() {
        User user = registerAndValidateWorker();
        return userService.login(new LoginRequest(user.getEmail(), "clave123"));
    }

    private User registerAndValidateClient() {
        RegisterUserResult result = userService.registerUser(new RegisterUserRequest(
                "Guillermina",
                "Fiore",
                "12345678",
                "2610000000",
                "guillermina@example.com",
                "clave123",
                "clave123",
                UserRole.CLIENT));

        return userService.validateAccount(result.accountValidationToken());
    }

    private User registerAndValidateWorker() {
        RegisterUserResult result = userService.registerUser(new RegisterUserRequest(
                "Joaquin",
                "Becerra",
                "22333444",
                "2612222222",
                "joaquin@example.com",
                "clave123",
                "clave123",
                UserRole.WORKER));

        return userService.validateAccount(result.accountValidationToken());
    }

    private CreateClientProfileRequest validClientRequest() {
        return new CreateClientProfileRequest(
                "https://cdn.example.com/cliente.png",
                "2615555555",
                "San Martin",
                "123",
                "2",
                "A",
                "5500",
                "Godoy Cruz",
                "Mendoza",
                "Limpieza semanal por la manana");
    }

    private CreateWorkerProfileRequest validWorkerRequest() {
        return new CreateWorkerProfileRequest(
                null,
                "Limpieza profunda y mantenimiento del hogar",
                3,
                "Limpieza general y profunda",
                "Godoy Cruz y Ciudad de Mendoza",
                "Lunes a viernes de 8 a 16",
                new BigDecimal("4000.00"));
    }

    private void authenticate(LoginResult session) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                AuthenticatedUserPrincipal.from(session.user()),
                session.sessionToken(),
                List.of(new SimpleGrantedAuthority("ROLE_" + session.user().getRole().name())));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}

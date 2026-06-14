package com.ritualfresh.auth;

import com.ritualfresh.auth.dto.ConfirmPasswordResetRequest;
import com.ritualfresh.auth.dto.LoginRequest;
import com.ritualfresh.auth.dto.LoginResult;
import com.ritualfresh.auth.dto.PasswordResetRequest;
import com.ritualfresh.auth.dto.PasswordResetResult;
import com.ritualfresh.auth.dto.RegisterUserRequest;
import com.ritualfresh.auth.dto.RegisterUserResult;
import com.ritualfresh.auth.model.AccountStatus;
import com.ritualfresh.auth.model.User;
import com.ritualfresh.auth.model.UserRole;
import com.ritualfresh.auth.repository.InMemoryUserRepository;
import com.ritualfresh.auth.repository.InMemoryUserSessionRepository;
import com.ritualfresh.auth.repository.UserRepository;
import com.ritualfresh.auth.repository.UserSessionRepository;
import com.ritualfresh.auth.service.UserService;
import com.ritualfresh.shared.exception.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceTest {
    private UserService userService;

    @BeforeEach
    void setUp() {
        UserRepository userRepository = new InMemoryUserRepository();
        userService = new UserService(userRepository, new InMemoryUserSessionRepository());
    }

    @Test
    void us01M01Rf01RegistersClientUserWithPendingAccount() {
        RegisterUserResult result = registerClient();

        assertEquals(AccountStatus.PENDING_VALIDATION, result.user().getAccountStatus());
        assertEquals(UserRole.CLIENT, result.user().getRole());
        assertNotNull(result.accountValidationToken());
    }

    @Test
    void us01M01Rf01PreventsRegisteringAnExistingEmail() {
        registerClient();

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> userService.registerUser(new RegisterUserRequest(
                "Otra",
                "Persona",
                "87654321",
                "2611111111",
                "guillermina@example.com",
                "clave123",
                "clave123",
                UserRole.WORKER)));

        assertEquals("El correo ya se encuentra registrado.", exception.getMessage());
    }

    @Test
    void us01M01Rf01PreventsRegisteringWithDifferentPasswords() {
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> userService.registerUser(new RegisterUserRequest(
                "Guillermina",
                "Fiore",
                "12345678",
                "2610000000",
                "guillermina@example.com",
                "clave123",
                "otraClave",
                UserRole.CLIENT)));

        assertEquals("Las contrasenas no coinciden.", exception.getMessage());
    }

    @Test
    void us02M01Rf02PreventsLoginBeforeAccountValidation() {
        registerClient();

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> userService.login(new LoginRequest(
                "guillermina@example.com",
                "clave123")));

        assertEquals("Debe validar su cuenta antes de iniciar sesion.", exception.getMessage());
    }

    @Test
    void us02M01Rf02AllowsLoginAfterValidationAndGeneratesSession() {
        RegisterUserResult result = registerClient();
        User validatedUser = userService.validateAccount(result.accountValidationToken());

        LoginResult loginResult = userService.login(new LoginRequest(
                "guillermina@example.com",
                "clave123"));
        User authenticatedUser = userService.getAuthenticatedUser(loginResult.sessionToken());

        assertEquals(AccountStatus.ACTIVE, validatedUser.getAccountStatus());
        assertEquals(UserRole.CLIENT, loginResult.user().getRole());
        assertEquals(validatedUser.getId(), authenticatedUser.getId());
        assertNotNull(loginResult.sessionToken());
        assertNotNull(loginResult.sessionExpiresAt());
    }

    @Test
    void us02M01Rf02PreventsLoginWithIncorrectCredentials() {
        RegisterUserResult result = registerClient();
        userService.validateAccount(result.accountValidationToken());

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> userService.login(new LoginRequest(
                "guillermina@example.com",
                "incorrecta")));

        assertEquals("El correo o la contrasena son incorrectos.", exception.getMessage());
    }

    @Test
    void us03M01Rf03GeneratesResetTokenAndAllowsPasswordChange() {
        RegisterUserResult result = registerClient();
        userService.validateAccount(result.accountValidationToken());

        PasswordResetResult reset = userService.requestPasswordReset(
                new PasswordResetRequest("guillermina@example.com"));
        userService.confirmPasswordReset(new ConfirmPasswordResetRequest(
                reset.resetToken(),
                "nuevaClave123",
                "nuevaClave123"));

        LoginResult loginResult = userService.login(new LoginRequest(
                "guillermina@example.com",
                "nuevaClave123"));

        assertNotNull(reset.resetToken());
        assertNotNull(loginResult.sessionToken());
    }

    @Test
    void us03M01Rf03PreventsResetForUnknownEmail() {
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> userService.requestPasswordReset(
                new PasswordResetRequest("nadie@example.com")));

        assertEquals("No existe una cuenta asociada al correo ingresado.", exception.getMessage());
    }

    private RegisterUserResult registerClient() {
        return userService.registerUser(new RegisterUserRequest(
                "Guillermina",
                "Fiore",
                "12345678",
                "2610000000",
                "guillermina@example.com",
                "clave123",
                "clave123",
                UserRole.CLIENT));
    }
}

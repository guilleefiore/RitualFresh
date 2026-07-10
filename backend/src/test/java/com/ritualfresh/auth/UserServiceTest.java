package com.ritualfresh.auth;

import com.ritualfresh.auth.dto.ConfirmPasswordResetRequest;
import com.ritualfresh.auth.dto.LoginRequest;
import com.ritualfresh.auth.dto.LoginResult;
import com.ritualfresh.auth.dto.OAuth2ProfileData;
import com.ritualfresh.auth.dto.PasswordResetRequest;
import com.ritualfresh.auth.dto.PasswordResetResult;
import com.ritualfresh.auth.dto.RegisterUserRequest;
import com.ritualfresh.auth.dto.RegisterUserResult;
import com.ritualfresh.auth.model.AccountStatus;
import com.ritualfresh.auth.model.User;
import com.ritualfresh.auth.model.UserRole;
import com.ritualfresh.auth.security.PasswordSecurity;
import com.ritualfresh.notifications.InMemoryAccountEmailService;
import com.ritualfresh.auth.repository.InMemoryUserRepository;
import com.ritualfresh.auth.repository.InMemoryUserSessionRepository;
import com.ritualfresh.auth.repository.UserRepository;
import com.ritualfresh.auth.repository.UserSessionRepository;
import com.ritualfresh.auth.service.UserService;
import com.ritualfresh.shared.exception.BusinessRuleException;
import com.ritualfresh.shared.security.AuthenticatedUserPrincipal;
import com.ritualfresh.profiles.dto.CreateClientProfileRequest;
import com.ritualfresh.profiles.repository.ClientProfileRepository;
import com.ritualfresh.profiles.repository.InMemoryClientProfileRepository;
import com.ritualfresh.profiles.repository.InMemoryWorkerProfileRepository;
import com.ritualfresh.profiles.repository.WorkerProfileRepository;
import com.ritualfresh.profiles.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceTest {
    private UserService userService;
    private InMemoryAccountEmailService accountEmailService;
    private UserRepository userRepository;
    private ClientProfileRepository clientProfileRepository;
    private WorkerProfileRepository workerProfileRepository;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        userRepository = new InMemoryUserRepository();
        UserSessionRepository userSessionRepository = new InMemoryUserSessionRepository();
        clientProfileRepository = new InMemoryClientProfileRepository();
        workerProfileRepository = new InMemoryWorkerProfileRepository();
        accountEmailService = new InMemoryAccountEmailService();
        userService = new UserService(userRepository, userSessionRepository, accountEmailService, clientProfileRepository, workerProfileRepository);
    }

    @Test
    void us01M01Rf01RegistersClientUserWithPendingAccount() {
        RegisterUserResult result = registerClient();

        assertEquals(AccountStatus.PENDING_VALIDATION, result.user().getAccountStatus());
        assertEquals(UserRole.CLIENT, result.user().getRole());
        assertNotNull(result.accountValidationToken());
        assertEquals(1, accountEmailService.getValidationTokens().size());
    }

    @Test
    void us01M01Rf01RegistersManualAccountWithoutProfileContactData() {
        RegisterUserResult result = userService.registerUser(new RegisterUserRequest(
                "guillermina.dni@example.com",
                "Clave123",
                "Clave123",
                UserRole.CLIENT));

        assertEquals("", result.user().getDocumentNumber());
        assertEquals("", result.user().getPhoneNumber());
        assertEquals("Pendiente", result.user().getFirstName());
        assertEquals("Completar", result.user().getLastName());
    }

    @Test
    void us01M01Rf01AllowsRegisteringAgainWhenPreviousAccountWasDeleted() {
        RegisterUserResult original = registerClient();
        userService.validateAccount(original.accountValidationToken());
        User deletedUser = userRepository.findByEmail("guillermina@example.com").orElseThrow();
        Long originalId = deletedUser.getId();
        deletedUser.deactivate();
        userRepository.save(deletedUser);

        RegisterUserResult restored = userService.registerUser(new RegisterUserRequest(
                "guillermina@example.com",
                "NuevaClave123",
                "NuevaClave123",
                UserRole.WORKER));

        assertEquals(originalId, restored.user().getId());
        assertEquals(AccountStatus.PENDING_VALIDATION, restored.user().getAccountStatus());
        assertEquals(UserRole.WORKER, restored.user().getRole());
        assertEquals("Pendiente", restored.user().getFirstName());
        assertEquals(2, accountEmailService.getValidationTokens().size());
    }

    @Test
    void us01M01Rf01RejectsWeakPassword() {
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> userService.registerUser(new RegisterUserRequest(
                "guillermina.password@example.com",
                "clave",
                "clave",
                UserRole.CLIENT)));

        assertEquals("La contrasena debe tener al menos 8 caracteres, una mayuscula, una minuscula y un numero.", exception.getMessage());
    }

    @Test
    void us01M01Rf01PreventsRegisteringAnExistingEmail() {
        registerClient();

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> userService.registerUser(new RegisterUserRequest(
                "guillermina@example.com",
                "Clave123",
                "Clave123",
                UserRole.WORKER)));

        assertEquals("El correo ya se encuentra registrado.", exception.getMessage());
    }

    @Test
    void us01M01Rf01PreventsRegisteringWithDifferentPasswords() {
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> userService.registerUser(new RegisterUserRequest(
                "guillermina@example.com",
                "Clave123",
                "OtraClave123",
                UserRole.CLIENT)));

        assertEquals("Las contrasenas no coinciden.", exception.getMessage());
    }

    @Test
    void us02M01Rf02PreventsLoginBeforeAccountValidation() {
        registerClient();

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> userService.login(new LoginRequest(
                "guillermina@example.com",
                "Clave123")));

        assertEquals("Debe validar su cuenta antes de iniciar sesion.", exception.getMessage());
    }

    @Test
    void us02M01Rf02AllowsLoginAfterValidationAndGeneratesSession() {
        RegisterUserResult result = registerClient();
        User validatedUser = userService.validateAccount(result.accountValidationToken());

        LoginResult loginResult = userService.login(new LoginRequest(
                "guillermina@example.com",
                "Clave123"));
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
    void us02M01Rf02CreatesGoogleAccountWithActiveSession() {
        LoginResult loginResult = userService.loginWithGoogle(new OAuth2ProfileData(
                "google.client@example.com",
                "Guillermina",
                "Fiore"));

        User user = userRepository.findByEmail("google.client@example.com").orElseThrow();

        assertEquals(AccountStatus.ACTIVE, user.getAccountStatus());
        assertEquals(UserRole.CLIENT, user.getRole());
        assertNotNull(loginResult.sessionToken());
        assertNotNull(loginResult.sessionExpiresAt());
    }

    @Test
    void choosingCurrentRoleIsIdempotentForGoogleOnboarding() {
        LoginResult loginResult = userService.loginWithGoogle(new OAuth2ProfileData(
                "google.client@example.com",
                "Guillermina",
                "Fiore"));

        LoginResult updated = userService.updateUserRole(loginResult.sessionToken(), UserRole.CLIENT);

        assertEquals(UserRole.CLIENT, updated.user().getRole());
        assertEquals(loginResult.sessionToken(), updated.sessionToken());
    }

    @Test
    void cannotChangeRoleAfterCreatingProfile() {
        LoginResult session = userService.loginWithGoogle(new OAuth2ProfileData(
                "profile.client@example.com",
                "Guillermina",
                "Fiore"));
        authenticate(session);
        ProfileService profileService = new ProfileService(userService, clientProfileRepository, workerProfileRepository);
        profileService.createClientProfile(new CreateClientProfileRequest(
                "Guillermina",
                "Fiore",
                "/uploads/client.png",
                "2615555555",
                "San Martin",
                "123",
                null,
                null,
                "5500",
                "Godoy Cruz",
                "Mendoza",
                "Limpieza semanal"));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> userService.updateUserRole(
                session.sessionToken(),
                UserRole.WORKER));

        assertEquals("No puede cambiar el rol porque ya posee un perfil creado.", exception.getMessage());
    }

    @Test
    void us02M01Rf02ReusesPendingAccountWhenLoggingInWithGoogle() {
        RegisterUserResult result = registerClient();

        LoginResult loginResult = userService.loginWithGoogle(new OAuth2ProfileData(
                "guillermina@example.com",
                "Guillermina",
                "Fiore"));

        User user = userRepository.findByEmail("guillermina@example.com").orElseThrow();

        assertEquals(AccountStatus.ACTIVE, user.getAccountStatus());
        assertEquals(result.user().getId(), user.getId());
        assertNotNull(loginResult.sessionToken());
    }

    @Test
    void us02M01Rf02RestoresDeletedAccountWhenLoggingInWithGoogle() {
        RegisterUserResult result = registerClient();
        User deletedUser = result.user();
        Long originalId = deletedUser.getId();
        deletedUser.deactivate();
        userRepository.save(deletedUser);

        LoginResult loginResult = userService.loginWithGoogle(new OAuth2ProfileData(
                "guillermina@example.com",
                "Google",
                "User"));

        User restored = userRepository.findByEmail("guillermina@example.com").orElseThrow();
        assertEquals(originalId, restored.getId());
        assertEquals(AccountStatus.ACTIVE, restored.getAccountStatus());
        assertEquals(UserRole.CLIENT, restored.getRole());
        assertEquals("Google", restored.getFirstName());
        assertNotNull(loginResult.sessionToken());
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
        assertEquals(1, accountEmailService.getResetTokens().size());
    }

    @Test
    void us03M01Rf03PreventsWeakPasswordReset() {
        RegisterUserResult result = registerClient();
        userService.validateAccount(result.accountValidationToken());
        PasswordResetResult reset = userService.requestPasswordReset(
                new PasswordResetRequest("guillermina@example.com"));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> userService.confirmPasswordReset(
                new ConfirmPasswordResetRequest(reset.resetToken(), "debil", "debil")));

        assertEquals("La contrasena debe tener al menos 8 caracteres, una mayuscula, una minuscula y un numero.", exception.getMessage());
    }

    @Test
    void us03M01Rf03PreventsResetForUnknownEmail() {
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> userService.requestPasswordReset(
                new PasswordResetRequest("nadie@example.com")));

        assertEquals("No existe una cuenta asociada al correo ingresado.", exception.getMessage());
    }

    @Test
    void authenticatedUserCanDeleteOwnAccountAndCurrentSession() {
        RegisterUserResult result = registerClient();
        userService.validateAccount(result.accountValidationToken());
        LoginResult loginResult = userService.login(new LoginRequest(
                "guillermina@example.com",
                "Clave123"));
        authenticate(loginResult);

        userService.deleteAuthenticatedAccount(loginResult.sessionToken());

        User deletedUser = userRepository.findByEmail("guillermina@example.com").orElseThrow();
        assertEquals(AccountStatus.DELETED, deletedUser.getAccountStatus());
        assertTrue(deletedUser.getDeactivatedAt() != null);

        BusinessRuleException loginException = assertThrows(BusinessRuleException.class, () -> userService.login(new LoginRequest(
                "guillermina@example.com",
                "Clave123")));
        assertEquals("La cuenta no se encuentra activa.", loginException.getMessage());

        BusinessRuleException sessionException = assertThrows(BusinessRuleException.class, () -> userService.getAuthenticatedUser(loginResult.sessionToken()));
        assertEquals("La sesion expiro. Debe iniciar sesion nuevamente.", sessionException.getMessage());
    }

    @Test
    void authenticatedUserCannotCloseAnotherUsersSession() {
        RegisterUserResult firstUser = registerClient();
        userService.validateAccount(firstUser.accountValidationToken());
        LoginResult firstSession = userService.login(new LoginRequest("guillermina@example.com", "Clave123"));

        RegisterUserResult secondUser = userService.registerUser(new RegisterUserRequest(
                "otra@example.com",
                "Clave123",
                "Clave123",
                UserRole.CLIENT));
        userService.validateAccount(secondUser.accountValidationToken());
        LoginResult secondSession = userService.login(new LoginRequest("otra@example.com", "Clave123"));

        authenticate(firstSession);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class,
                () -> userService.closeSession(secondSession.sessionToken()));

        assertEquals("La sesion indicada no pertenece al usuario autenticado.", exception.getMessage());
        assertEquals(firstSession.sessionToken(), userService.getAuthenticatedSessionToken());
        assertEquals("otra@example.com", userService.getAuthenticatedUser(secondSession.sessionToken()).getEmail());
    }

    @Test
    void us01M01Rf01RejectsExpiredAccountValidationToken() {
        RegisterUserResult result = registerClientWithValidationExpiration(LocalDateTime.now().minusHours(1));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class,
                () -> userService.validateAccount(result.accountValidationToken()));

        assertEquals("El enlace de validacion no es valido o expiro.", exception.getMessage());
    }

    @Test
    void us01M01Rf01ResendsValidationEmailAndRefreshesToken() {
        RegisterUserResult result = registerClientWithValidationExpiration(LocalDateTime.now().minusHours(1));
        String originalToken = result.accountValidationToken();

        userService.resendAccountValidation("guillermina@example.com");

        User updatedUser = userRepository.findByEmail("guillermina@example.com").orElseThrow();
        String refreshedToken = updatedUser.getAccountValidationToken();

        assertNotNull(refreshedToken);
        assertTrue(accountEmailService.getValidationTokens().size() == 1);
        assertTrue(!originalToken.equals(refreshedToken));

        BusinessRuleException oldTokenException = assertThrows(BusinessRuleException.class,
                () -> userService.validateAccount(originalToken));
        assertEquals("El enlace de validacion no es valido o expiro.", oldTokenException.getMessage());

        User validatedUser = userService.validateAccount(refreshedToken);
        assertEquals(AccountStatus.ACTIVE, validatedUser.getAccountStatus());
    }

    private RegisterUserResult registerClient() {
        return userService.registerUser(new RegisterUserRequest(
                "guillermina@example.com",
                "Clave123",
                "Clave123",
                UserRole.CLIENT));
    }

    private RegisterUserResult registerClientWithValidationExpiration(LocalDateTime accountValidationTokenExpiresAt) {
        String accountValidationToken = UUID.randomUUID().toString();
        User user = User.register(new User.RegistrationData(
                "Guillermina",
                "Fiore",
                "guillermina@example.com",
                PasswordSecurity.generateHash("Clave123"),
                UserRole.CLIENT,
                LocalDateTime.now(),
                accountValidationToken,
                accountValidationTokenExpiresAt));
        userRepository.save(user);

        return new RegisterUserResult(
                user,
                "Registro exitoso. Revise su correo para validar la cuenta.",
                accountValidationToken);
    }

    private void authenticate(LoginResult session) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                AuthenticatedUserPrincipal.from(session.user()),
                session.sessionToken(),
                List.of(new SimpleGrantedAuthority("ROLE_" + session.user().getRole().name())));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}

package com.ritualfresh.auth.service;

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
import com.ritualfresh.auth.model.UserSession;
import com.ritualfresh.auth.repository.UserRepository;
import com.ritualfresh.auth.repository.UserSessionRepository;
import com.ritualfresh.auth.security.PasswordSecurity;
import com.ritualfresh.notifications.service.AccountEmailService;
import com.ritualfresh.shared.security.AuthenticatedUserPrincipal;
import com.ritualfresh.shared.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
// Servicio de aplicacion para los casos de uso de autenticacion y gestion de cuenta.
public class UserService {
    // Patron minimo para validar formato de email.
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    // Acepta DNI argentino de 7 u 8 digitos, con o sin puntos.
    private static final Pattern ARGENTINE_DNI_PATTERN = Pattern.compile("^(?:\\d{7,8}|\\d{1,2}\\.\\d{3}\\.\\d{3})$");
    // Acepta telefonos con prefijo opcional, espacios, guiones y parentesis.
    private static final Pattern PHONE_ALLOWED_CHARS_PATTERN = Pattern.compile("^[\\d\\s()+-]+$");
    // Reglas minimas de seguridad para contrasenas publicas.
    private static final Pattern PASSWORD_UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern PASSWORD_LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
    private static final Pattern PASSWORD_DIGIT_PATTERN = Pattern.compile(".*\\d.*");
    // Duracion estandar de una sesion iniciada correctamente.
    private static final int SESSION_DURATION_HOURS = 8;
    // Duracion del token de validacion de cuenta.
    private static final int ACCOUNT_VALIDATION_DURATION_HOURS = 24;
    // Tiempo maximo de validez para recuperar una contrasena.
    private static final int PASSWORD_RESET_DURATION_HOURS = 1;
    // Etiquetas reutilizadas en validaciones para evitar duplicar literales.
    private static final String EMAIL_FIELD = "correo electronico";
    private static final String CREDENTIAL_FIELD = "contrasena";
    private static final String PENDING_FIRST_NAME = "Pendiente";
    private static final String PENDING_LAST_NAME = "Completar";

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final AccountEmailService accountEmailService;

    @Transactional
    // Registra un usuario nuevo, valida los datos y deja la cuenta pendiente de validacion.
    public RegisterUserResult registerUser(RegisterUserRequest request) { // RegisterUserResult -> DTO.
        validateRegistration(request);

        String normalizedEmail = normalizeEmail(request.email());
        String accountValidationToken = UUID.randomUUID().toString();
        LocalDateTime accountValidationTokenExpiresAt = LocalDateTime.now().plusHours(ACCOUNT_VALIDATION_DURATION_HOURS);
        User.RegistrationData registrationData = new User.RegistrationData(
                PENDING_FIRST_NAME,
                PENDING_LAST_NAME,
                normalizedEmail,
                PasswordSecurity.generateHash(request.password()),
                request.role(),
                LocalDateTime.now(),
                accountValidationToken,
                accountValidationTokenExpiresAt);
        User user = userRepository.findByEmail(normalizedEmail)
                .map(existingUser -> {
                    existingUser.restoreRegistration(registrationData);
                    return existingUser;
                })
                .orElseGet(() -> User.register(registrationData));

        userRepository.save(user);
        accountEmailService.sendAccountValidationEmail(user, accountValidationToken, accountValidationTokenExpiresAt); // envía mail de validación.

        return new RegisterUserResult( // RegisterUserResult -> DTO.
                user,
                "Registro exitoso. Revise su correo para validar la cuenta.",
                accountValidationToken);
    }

    @Transactional
    // Activa la cuenta a partir del token enviado durante el registro.
    public User validateAccount(String accountValidationToken) {
        User user = userRepository.findByAccountValidationToken(accountValidationToken)
                .orElseThrow(() -> new BusinessRuleException("El enlace de validacion no es valido o expiro."));

        if (!user.hasValidAccountValidationToken(LocalDateTime.now())) {
            throw new BusinessRuleException("El enlace de validacion no es valido o expiro.");
        }

        user.validateAccount();
        userRepository.save(user);

        return user;
    }

    @Transactional
    // Reenvia un nuevo enlace de validacion a una cuenta que sigue pendiente.
    public void resendAccountValidation(String email) {
        validateAccountValidationResend(email);

        User user = userRepository.findByEmail(normalizeEmail(email))
                .orElseThrow(() -> new BusinessRuleException("No existe una cuenta pendiente de validacion asociada al correo ingresado."));

        if (user.getAccountStatus() != AccountStatus.PENDING_VALIDATION) {
            throw new BusinessRuleException("No existe una cuenta pendiente de validacion asociada al correo ingresado.");
        }

        String accountValidationToken = UUID.randomUUID().toString();
        LocalDateTime accountValidationTokenExpiresAt = LocalDateTime.now().plusHours(ACCOUNT_VALIDATION_DURATION_HOURS);
        user.startAccountValidation(accountValidationToken, accountValidationTokenExpiresAt);
        userRepository.save(user);
        accountEmailService.sendAccountValidationEmail(user, accountValidationToken, accountValidationTokenExpiresAt);
    }

    @Transactional
    // Autentica al usuario y crea una nueva sesion con vencimiento.
    public LoginResult login(LoginRequest request) {
        validateLogin(request);

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessRuleException("El correo o la contrasena son incorrectos."));

        if (!PasswordSecurity.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessRuleException("El correo o la contrasena son incorrectos.");
        }

        if (user.getAccountStatus() == AccountStatus.PENDING_VALIDATION) {
            throw new BusinessRuleException("Debe validar su cuenta antes de iniciar sesion.");
        }

        if (!user.isActive()) {
            throw new BusinessRuleException("La cuenta no se encuentra activa.");
        }

        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = createdAt.plusHours(SESSION_DURATION_HOURS);
        String sessionToken = UUID.randomUUID().toString();
        userSessionRepository.save(new UserSession(user, sessionToken, createdAt, expiresAt));

        return new LoginResult(user, sessionToken, expiresAt);
    }

    @Transactional
    // Autentica o crea un usuario local a partir del perfil OAuth de Google.
    public LoginResult loginWithGoogle(OAuth2ProfileData profileData) {
        if (profileData == null) {
            throw new BusinessRuleException("No se pudo iniciar sesion con Google.");
        }

        String normalizedEmail = normalizeEmail(profileData.email());
        if (!isValidEmail(normalizedEmail)) {
            throw new BusinessRuleException("No se pudo iniciar sesion con Google.");
        }

        String firstName = normalizeProfilePart(profileData.firstName(), "Usuario");
        String lastName = normalizeProfilePart(profileData.lastName(), "Google");
        String documentNumber = generateSyntheticDocumentNumber(normalizedEmail);
        String phoneNumber = generateSyntheticPhoneNumber(normalizedEmail);
        User.OAuthAccountData oauthAccountData = new User.OAuthAccountData(
                firstName,
                lastName,
                documentNumber,
                phoneNumber,
                normalizedEmail,
                PasswordSecurity.generateHash(UUID.randomUUID().toString()),
                UserRole.CLIENT,
                LocalDateTime.now());
        boolean[] isNewUser = {false};
        User user = userRepository.findByEmail(normalizedEmail).orElseGet(() -> {
            isNewUser[0] = true;
            return User.oauthAccount(oauthAccountData);
        });

        if (user.getAccountStatus() == AccountStatus.PENDING_VALIDATION) {
            user.validateAccount();
        } else if (user.getAccountStatus() == AccountStatus.DELETED) {
            isNewUser[0] = true;
            user.restoreOAuthAccount(oauthAccountData);
        } else if (!user.isActive()) {
            throw new BusinessRuleException("La cuenta no se encuentra activa.");
        }

        userRepository.save(user);

        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = createdAt.plusHours(SESSION_DURATION_HOURS);
        String sessionToken = UUID.randomUUID().toString();
        userSessionRepository.save(new UserSession(user, sessionToken, createdAt, expiresAt));

        return new LoginResult(user, sessionToken, expiresAt, isNewUser[0]);
    }
    @Transactional
    // Cambia el rol del usuario autenticado (CLIENT o WORKER).
    public LoginResult updateUserRole(String sessionToken, UserRole newRole) {
        if (newRole != UserRole.CLIENT && newRole != UserRole.WORKER) {
            throw new BusinessRuleException("El rol debe ser cliente o trabajador.");
        }

        UserSession session = userSessionRepository.findByToken(sessionToken)
                .orElseThrow(() -> new BusinessRuleException("Debe iniciar sesion para acceder a esta funcionalidad."));

        if (!session.isActive(LocalDateTime.now())) {
            throw new BusinessRuleException("La sesion expiro. Debe iniciar sesion nuevamente.");
        }

        User user = session.getUser();
        if (user.getRole() == newRole) {
            throw new BusinessRuleException("El usuario ya posee el rol seleccionado.");
        }

        user.setRole(newRole);
        userRepository.save(user);

        return new LoginResult(user, session.getToken(), session.getExpiresAt());
    }

    @Transactional(readOnly = true) // sólo lee de la BD, no modifica.
    // Resuelve el usuario autenticado a partir de un token de sesion.
    public User getAuthenticatedUser(String sessionToken) {
        return getActiveUserFromSessionToken(sessionToken);
    }

    @Transactional(readOnly = true)
    // Resuelve la sesion activa del usuario autenticado a partir del token de sesion.
    public LoginResult getAuthenticatedSession(String sessionToken) {
        UserSession session = userSessionRepository.findByToken(sessionToken)
                .orElseThrow(() -> new BusinessRuleException("Debe iniciar sesion para acceder a esta funcionalidad."));

        if (!session.isActive(LocalDateTime.now())) {
            throw new BusinessRuleException("La sesion expiro. Debe iniciar sesion nuevamente.");
        }

        if (!session.getUser().isActive()) {
            throw new BusinessRuleException("La cuenta no se encuentra activa.");
        }

        return new LoginResult(session.getUser(), session.getToken(), session.getExpiresAt());
    }

    @Transactional
    // Cierra una sesion existente marcando el momento de cierre.
    public void closeSession(String sessionToken) {
        UserSession session = requireAuthenticatedSession(sessionToken);
        session.close(LocalDateTime.now());
        userSessionRepository.save(session);
    }

    @Transactional
    // Permite que el usuario autenticado desactive su propia cuenta y cierre su sesion actual.
    public void deleteAuthenticatedAccount(String sessionToken) {
        UserSession session = requireAuthenticatedSession(sessionToken);
        User user = session.getUser();
        session.close(LocalDateTime.now());
        userSessionRepository.save(session);

        user.deactivate();
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    // Obtiene el usuario actual desde el principal cargado en Spring Security.
    public User getAuthenticatedUser() {
        return getAuthenticatedUserFromSecurityContext();
    }

    // Obtiene el usuario actual validando que siga activo.
    private User getAuthenticatedUserFromSecurityContext() {
        AuthenticatedUserPrincipal principal = getCurrentPrincipal();
        User user = userRepository.findById(principal.getUserId())
                .orElseThrow(() -> new BusinessRuleException("Debe iniciar sesion para acceder a esta funcionalidad."));

        if (!user.isActive()) {
            throw new BusinessRuleException("La cuenta no se encuentra activa.");
        }

        return user;
    }

    // Resuelve la sesion por token y centraliza el mensaje de error si no existe.
    private UserSession requireSessionByToken(String sessionToken, String notFoundMessage) {
        return userSessionRepository.findByToken(sessionToken)
                .orElseThrow(() -> new BusinessRuleException(notFoundMessage));
    }

    // Resuelve una sesion y comprueba que pertenezca al usuario autenticado actual.
    private UserSession requireAuthenticatedSession(String sessionToken) {
        if (sessionToken == null || sessionToken.isBlank()) {
            throw new BusinessRuleException("Debe iniciar sesion para acceder a esta funcionalidad.");
        }

        User authenticatedUser = getAuthenticatedUserFromSecurityContext();
        UserSession session = requireSessionByToken(sessionToken, "La sesion indicada no existe.");

        if (!session.getUser().getId().equals(authenticatedUser.getId())) {
            throw new BusinessRuleException("La sesion indicada no pertenece al usuario autenticado.");
        }

        return session;
    }

    // Valida que la sesion siga vigente y que el usuario asociado permanezca activo.
    private User getActiveUserFromSessionToken(String sessionToken) {
        UserSession session = requireSessionByToken(sessionToken,
                "Debe iniciar sesion para acceder a esta funcionalidad.");

        if (!session.isActive(LocalDateTime.now())) {
            throw new BusinessRuleException("La sesion expiro. Debe iniciar sesion nuevamente.");
        }

        User user = session.getUser();
        if (!user.isActive()) {
            throw new BusinessRuleException("La cuenta no se encuentra activa.");
        }

        return user;
    }

    // Recupera el token de sesion actual desde el contexto de seguridad.
    public String getAuthenticatedSessionToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getCredentials() == null) {
            throw new BusinessRuleException("Debe iniciar sesion para acceder a esta funcionalidad.");
        }

        return authentication.getCredentials().toString();
    }

    @Transactional
    // Genera un token temporal para permitir el reseteo de contrasena.
    public PasswordResetResult requestPasswordReset(PasswordResetRequest request) {
        validatePasswordResetRequest(request);

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessRuleException("No existe una cuenta asociada al correo ingresado."));

        String resetToken = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(PASSWORD_RESET_DURATION_HOURS);
        user.startPasswordReset(resetToken, expiresAt);
        userRepository.save(user);
        accountEmailService.sendPasswordResetEmail(user, resetToken, expiresAt);

        return new PasswordResetResult(
                "Se envio el enlace de recuperacion al correo electronico indicado.",
                resetToken,
                expiresAt);
    }

    @Transactional
    // Confirma el cambio de contrasena usando el token de recuperacion.
    public User confirmPasswordReset(ConfirmPasswordResetRequest request) {
        validatePasswordResetConfirmation(request);

        User user = userRepository.findByPasswordResetToken(request.resetToken())
                .orElseThrow(() -> new BusinessRuleException("El enlace de recuperacion no es valido o expiro."));

        if (!user.hasValidPasswordResetToken(LocalDateTime.now())) {
            throw new BusinessRuleException("El enlace de recuperacion no es valido o expiro.");
        }

        user.changePassword(PasswordSecurity.generateHash(request.password()));
        userRepository.save(user);

        return user;
    }

    // Valida reglas de negocio del alta de usuario antes de persistirlo.
    private void validateRegistration(RegisterUserRequest request) {
        if (request == null) {
            throw new BusinessRuleException("Debe completar los datos de registro.");
        }

        validateRequired(request.email(), EMAIL_FIELD);
        validateRequired(request.password(), CREDENTIAL_FIELD);
        validateRequired(request.confirmPassword(), "confirmacion de contrasena");

        if (!isValidEmail(request.email())) {
            throw new BusinessRuleException("El correo ingresado no posee un formato valido.");
        }

        if (!isStrongPassword(request.password())) {
            throw new BusinessRuleException("La contrasena debe tener al menos 8 caracteres, una mayuscula, una minuscula y un numero.");
        }

        if (userRepository.findByEmail(request.email())
                .filter(user -> user.getAccountStatus() != AccountStatus.DELETED)
                .isPresent()) {
            throw new BusinessRuleException("El correo ya se encuentra registrado.");
        }

        if (!request.password().equals(request.confirmPassword())) {
            throw new BusinessRuleException("Las contrasenas no coinciden.");
        }

        if (request.role() != UserRole.CLIENT && request.role() != UserRole.WORKER) {
            throw new BusinessRuleException("Debe seleccionar el rol cliente o trabajador.");
        }
    }

    @Transactional
    public User updateAuthenticatedUserProfileData(String firstName, String lastName, String phoneNumber) {
        User user = getAuthenticatedUser();
        user.editData(
                validateRequiredProfileValue(firstName, "nombre"),
                validateRequiredProfileValue(lastName, "apellido"),
                user.getDocumentNumber(),
                phoneNumber == null ? user.getPhoneNumber() : phoneNumber.trim());
        userRepository.save(user);
        return user;
    }

    // Valida lo minimo necesario antes de intentar autenticar.
    private void validateLogin(LoginRequest request) {
        if (request == null) {
            throw new BusinessRuleException("Debe completar los datos de inicio de sesion.");
        }

        validateRequired(request.email(), EMAIL_FIELD);
        validateRequired(request.password(), CREDENTIAL_FIELD);

        if (!isValidEmail(request.email())) {
            throw new BusinessRuleException("El correo ingresado no posee un formato valido.");
        }
    }

    // Valida la solicitud inicial de recuperacion de contrasena.
    private void validatePasswordResetRequest(PasswordResetRequest request) {
        if (request == null) {
            throw new BusinessRuleException("Debe completar el correo electronico.");
        }

        validateRequired(request.email(), EMAIL_FIELD);

        if (!isValidEmail(request.email())) {
            throw new BusinessRuleException("El correo ingresado no posee un formato valido.");
        }
    }

    // Valida los datos enviados para confirmar el cambio de contrasena.
    private void validatePasswordResetConfirmation(ConfirmPasswordResetRequest request) {
        if (request == null) {
            throw new BusinessRuleException("Debe completar los datos de recuperacion.");
        }

        validateRequired(request.resetToken(), "token de recuperacion");
        validateRequired(request.password(), CREDENTIAL_FIELD);
        validateRequired(request.confirmPassword(), "confirmacion de contrasena");

        if (!request.password().equals(request.confirmPassword())) {
            throw new BusinessRuleException("Las contrasenas no coinciden.");
        }
    }

    // Valida la solicitud de reenvio del enlace de validacion.
    private void validateAccountValidationResend(String email) {
        validateRequired(email, EMAIL_FIELD);

        if (!isValidEmail(email)) {
            throw new BusinessRuleException("El correo ingresado no posee un formato valido.");
        }
    }

    // Centraliza la validacion de campos obligatorios de texto.
    private void validateRequired(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException("Debe completar el campo " + fieldName + ".");
        }
    }

    private String validateRequiredProfileValue(String value, String fieldName) {
        validateRequired(value, fieldName);
        return value.trim();
    }

    // Comprueba si el email cumple un formato basico aceptable.
    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    // Comprueba el formato basico del DNI argentino.
    private boolean isValidArgentineDni(String documentNumber) {
        return documentNumber != null && ARGENTINE_DNI_PATTERN.matcher(documentNumber.trim()).matches();
    }

    // Comprueba un telefono razonable para alta publica.
    private boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return false;
        }

        String trimmedPhoneNumber = phoneNumber.trim();
        if (!PHONE_ALLOWED_CHARS_PATTERN.matcher(trimmedPhoneNumber).matches()) {
            return false;
        }

        String digitsOnly = trimmedPhoneNumber.replaceAll("\\D", "");
        return digitsOnly.length() >= 10 && digitsOnly.length() <= 15;
    }

    // Valida una contrasena minima para cuentas publicas.
    private boolean isStrongPassword(String password) {
        if (password == null) {
            return false;
        }

        String trimmedPassword = password.trim();
        return trimmedPassword.length() >= 8
                && PASSWORD_UPPERCASE_PATTERN.matcher(trimmedPassword).matches()
                && PASSWORD_LOWERCASE_PATTERN.matcher(trimmedPassword).matches()
                && PASSWORD_DIGIT_PATTERN.matcher(trimmedPassword).matches();
    }

    // Normaliza el email para evitar diferencias por espacios o mayusculas.
    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    // Guarda el DNI como solo digitos para mantener consistencia en persistencia.
    private String normalizeDocumentNumber(String documentNumber) {
        return documentNumber == null ? "" : documentNumber.replace(".", "").trim();
    }

    private String normalizeProfilePart(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }

        return value.trim();
    }

    private String generateSyntheticDocumentNumber(String seed) {
        long positiveHash = Integer.toUnsignedLong(seed.hashCode());
        return String.format("%08d", positiveHash % 100_000_000L);
    }

    private String generateSyntheticPhoneNumber(String seed) {
        long positiveHash = Integer.toUnsignedLong((seed + ":phone").hashCode());
        return "261" + String.format("%07d", positiveHash % 10_000_000L);
    }

    // Obtiene el principal autenticado del contexto de Spring Security.
    private AuthenticatedUserPrincipal getCurrentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUserPrincipal principal)) {
            throw new BusinessRuleException("Debe iniciar sesion para acceder a esta funcionalidad.");
        }

        return principal;
    }
}

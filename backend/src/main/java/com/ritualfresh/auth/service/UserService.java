package com.ritualfresh.auth.service;

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
import com.ritualfresh.auth.model.UserSession;
import com.ritualfresh.auth.repository.UserRepository;
import com.ritualfresh.auth.repository.UserSessionRepository;
import com.ritualfresh.auth.security.PasswordSecurity;
import com.ritualfresh.shared.security.AuthenticatedUserPrincipal;
import com.ritualfresh.shared.exception.BusinessRuleException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class UserService {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final int SESSION_DURATION_HOURS = 8;
    private static final int PASSWORD_RESET_DURATION_HOURS = 1;

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;

    public UserService(UserRepository userRepository, UserSessionRepository userSessionRepository) {
        this.userRepository = userRepository;
        this.userSessionRepository = userSessionRepository;
    }

    @Transactional
    public RegisterUserResult registerUser(RegisterUserRequest request) {
        validateRegistration(request);

        String normalizedEmail = normalizeEmail(request.email());
        String accountValidationToken = UUID.randomUUID().toString();
        User user = User.register(new User.RegistrationData(
                request.firstName().trim(),
                request.lastName().trim(),
                request.documentNumber().trim(),
                request.phoneNumber().trim(),
                normalizedEmail,
                PasswordSecurity.generateHash(request.password()),
                request.role(),
                LocalDateTime.now(),
                accountValidationToken));

        userRepository.save(user);

        return new RegisterUserResult(
                user,
                "Register exitoso. La cuenta queda pendiente de validacion.",
                accountValidationToken);
    }

    @Transactional
    public User validateAccount(String accountValidationToken) {
        User user = userRepository.findByAccountValidationToken(accountValidationToken)
                .orElseThrow(() -> new BusinessRuleException("El enlace de validacion no es valido o expiro."));

        user.validateAccount();
        userRepository.save(user);

        return user;
    }

    @Transactional
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

    @Transactional(readOnly = true)
    public User getAuthenticatedUser(String sessionToken) {
        UserSession session = userSessionRepository.findByToken(sessionToken)
                .orElseThrow(() -> new BusinessRuleException("Debe iniciar sesion para acceder a esta funcionalidad."));

        if (!session.isActive(LocalDateTime.now())) {
            throw new BusinessRuleException("La sesion expiro. Debe iniciar sesion nuevamente.");
        }

        User user = session.getUser();
        if (!user.isActive()) {
            throw new BusinessRuleException("La cuenta no se encuentra activa.");
        }

        return user;
    }

    @Transactional
    public void closeSession(String sessionToken) {
        UserSession session = userSessionRepository.findByToken(sessionToken)
                .orElseThrow(() -> new BusinessRuleException("La sesion indicada no existe."));
        session.close(LocalDateTime.now());
        userSessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public User getAuthenticatedUser() {
        AuthenticatedUserPrincipal principal = getCurrentPrincipal();
        User user = userRepository.findById(principal.getUserId())
                .orElseThrow(() -> new BusinessRuleException("Debe iniciar sesion para acceder a esta funcionalidad."));

        if (!user.isActive()) {
            throw new BusinessRuleException("La cuenta no se encuentra activa.");
        }

        return user;
    }

    public String getAuthenticatedSessionToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getCredentials() == null) {
            throw new BusinessRuleException("Debe iniciar sesion para acceder a esta funcionalidad.");
        }

        return authentication.getCredentials().toString();
    }

    @Transactional
    public PasswordResetResult requestPasswordReset(PasswordResetRequest request) {
        validatePasswordResetRequest(request);

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessRuleException("No existe una cuenta asociada al correo ingresado."));

        String resetToken = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(PASSWORD_RESET_DURATION_HOURS);
        user.startPasswordReset(resetToken, expiresAt);
        userRepository.save(user);

        return new PasswordResetResult(
                "Se envio el enlace de recuperacion al correo electronico indicado.",
                resetToken,
                expiresAt);
    }

    @Transactional
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

    private void validateRegistration(RegisterUserRequest request) {
        if (request == null) {
            throw new BusinessRuleException("Debe completar los datos de registro.");
        }

        validateRequired(request.firstName(), "nombre");
        validateRequired(request.lastName(), "apellido");
        validateRequired(request.documentNumber(), "DNI");
        validateRequired(request.phoneNumber(), "telefono");
        validateRequired(request.email(), "correo electronico");
        validateRequired(request.password(), "contrasena");
        validateRequired(request.confirmPassword(), "confirmacion de contrasena");

        if (!isValidEmail(request.email())) {
            throw new BusinessRuleException("El correo ingresado no posee un formato valido.");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessRuleException("El correo ya se encuentra registrado.");
        }

        if (!request.password().equals(request.confirmPassword())) {
            throw new BusinessRuleException("Las contrasenas no coinciden.");
        }

        if (request.role() != UserRole.CLIENT && request.role() != UserRole.WORKER) {
            throw new BusinessRuleException("Debe seleccionar el rol cliente o trabajador.");
        }
    }

    private void validateLogin(LoginRequest request) {
        if (request == null) {
            throw new BusinessRuleException("Debe completar los datos de inicio de sesion.");
        }

        validateRequired(request.email(), "correo electronico");
        validateRequired(request.password(), "contrasena");

        if (!isValidEmail(request.email())) {
            throw new BusinessRuleException("El correo ingresado no posee un formato valido.");
        }
    }

    private void validatePasswordResetRequest(PasswordResetRequest request) {
        if (request == null) {
            throw new BusinessRuleException("Debe completar el correo electronico.");
        }

        validateRequired(request.email(), "correo electronico");

        if (!isValidEmail(request.email())) {
            throw new BusinessRuleException("El correo ingresado no posee un formato valido.");
        }
    }

    private void validatePasswordResetConfirmation(ConfirmPasswordResetRequest request) {
        if (request == null) {
            throw new BusinessRuleException("Debe completar los datos de recuperacion.");
        }

        validateRequired(request.resetToken(), "token de recuperacion");
        validateRequired(request.password(), "contrasena");
        validateRequired(request.confirmPassword(), "confirmacion de contrasena");

        if (!request.password().equals(request.confirmPassword())) {
            throw new BusinessRuleException("Las contrasenas no coinciden.");
        }
    }

    private void validateRequired(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException("Debe completar el campo " + fieldName + ".");
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private AuthenticatedUserPrincipal getCurrentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUserPrincipal principal)) {
            throw new BusinessRuleException("Debe iniciar sesion para acceder a esta funcionalidad.");
        }

        return principal;
    }
}

package com.ritualfresh.auth.controller;

import com.ritualfresh.auth.dto.AccountValidationApiResponse;
import com.ritualfresh.auth.dto.ConfirmPasswordResetApiRequest;
import com.ritualfresh.auth.dto.ConfirmPasswordResetRequest;
import com.ritualfresh.auth.dto.LoginApiRequest;
import com.ritualfresh.auth.dto.LoginApiResponse;
import com.ritualfresh.auth.dto.LoginRequest;
import com.ritualfresh.auth.dto.LoginResult;
import com.ritualfresh.auth.dto.MessageApiResponse;
import com.ritualfresh.auth.dto.PasswordResetApiRequest;
import com.ritualfresh.auth.dto.PasswordResetApiResponse;
import com.ritualfresh.auth.dto.PasswordResetRequest;
import com.ritualfresh.auth.dto.PasswordResetResult;
import com.ritualfresh.auth.dto.RegisterUserApiRequest;
import com.ritualfresh.auth.dto.RegisterUserApiResponse;
import com.ritualfresh.auth.dto.RegisterUserRequest;
import com.ritualfresh.auth.dto.RegisterUserResult;
import com.ritualfresh.auth.dto.ResendAccountValidationApiRequest;
import com.ritualfresh.auth.dto.ResendAccountValidationApiResponse;
import com.ritualfresh.auth.dto.UpdateUserRoleApiRequest;
import com.ritualfresh.auth.dto.UserApiResponse;
import com.ritualfresh.auth.model.User;
import com.ritualfresh.auth.service.UserService;
import com.ritualfresh.shared.security.SessionCookieUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// Controlador REST para endpoints de usuarios (capa delgada: HTTP -> service)
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    // Crear cuenta (persiste usuario y envía email de validación)
    public RegisterUserApiResponse registerUser(@Valid @RequestBody RegisterUserApiRequest request) {
        RegisterUserResult result = userService.registerUser(new RegisterUserRequest(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.password(),
                request.confirmPassword(),
                request.role().toUserRole()));

        return new RegisterUserApiResponse(
                result.message(),
                UserApiResponse.from(result.user()));
    }

    @GetMapping("/validation")
    // Validar cuenta usando token enviado por email
    public AccountValidationApiResponse validateAccount(@RequestParam String token) {
        User user = userService.validateAccount(token);

        return new AccountValidationApiResponse(
                "Cuenta validada correctamente.",
                UserApiResponse.from(user));
    }

    @PostMapping("/validation/resend")
    // Reenviar email de validación
    public ResendAccountValidationApiResponse resendAccountValidation(
            @Valid @RequestBody ResendAccountValidationApiRequest request) {
        userService.resendAccountValidation(request.email());

        return new ResendAccountValidationApiResponse("Se envio un nuevo enlace de validacion al correo indicado.");
    }

    @PostMapping("/login")
    // Autenticar y establecer cookie de sesión (HttpOnly)
    public LoginApiResponse login(@Valid @RequestBody LoginApiRequest request, HttpServletResponse response) {
        LoginResult result = userService.login(new LoginRequest(
                request.email(),
                request.password()));
        response.addHeader(
                HttpHeaders.SET_COOKIE,
                SessionCookieUtils.buildSessionCookieHeader(result.sessionToken(), result.sessionExpiresAt()));

        return new LoginApiResponse(
                "Login de sesion exitoso.",
                result.sessionExpiresAt(),
                UserApiResponse.from(result.user()));
    }

    @GetMapping("/me")
    // Devuelve la sesion actual del usuario autenticado.
    public LoginApiResponse currentSession() {
        LoginResult result = userService.getAuthenticatedSession(userService.getAuthenticatedSessionToken());

        return new LoginApiResponse(
                "Sesion activa.",
                result.sessionExpiresAt(),
                UserApiResponse.from(result.user()));
    }

    @PostMapping("/password-reset")
    // Iniciar recuperación de contraseña (genera token y envía enlace)
    public PasswordResetApiResponse requestPasswordReset(@Valid @RequestBody PasswordResetApiRequest request) {
        PasswordResetResult result = userService.requestPasswordReset(
                new PasswordResetRequest(request.email()));

        return new PasswordResetApiResponse(
                result.message(),
                result.expiresAt());
    }

    @PostMapping("/password-reset/confirm")
    // Confirmar cambio de contraseña usando token
    public MessageApiResponse confirmPasswordReset(@Valid @RequestBody ConfirmPasswordResetApiRequest request) {
        userService.confirmPasswordReset(new ConfirmPasswordResetRequest(
                request.resetToken(),
                request.password(),
                request.confirmPassword()));

        return new MessageApiResponse("Password actualizada correctamente.");
    }

    @PostMapping("/logout")
    // Cerrar sesión: invalidar sesión y expirar cookie
    public MessageApiResponse closeSession(Authentication authentication, HttpServletResponse response) {
        userService.closeSession(extractSessionToken(authentication));
        response.addHeader(HttpHeaders.SET_COOKIE, SessionCookieUtils.buildExpiredSessionCookieHeader());

        return new MessageApiResponse("Session cerrada correctamente.");
    }

    @DeleteMapping("/me")
    // Eliminar propia cuenta (borrado lógico) y cerrar sesión
    public MessageApiResponse deleteMyAccount(Authentication authentication, HttpServletResponse response) {
        userService.deleteAuthenticatedAccount(extractSessionToken(authentication));
        response.addHeader(HttpHeaders.SET_COOKIE, SessionCookieUtils.buildExpiredSessionCookieHeader());

        return new MessageApiResponse("Cuenta eliminada correctamente.");
    }

    @PutMapping("/me/role")
    // Actualiza el rol del usuario autenticado (CLIENT o WORKER).
    public LoginApiResponse updateMyRole(
            Authentication authentication,
            @Valid @RequestBody UpdateUserRoleApiRequest request) {
        LoginResult result = userService.updateUserRole(
                extractSessionToken(authentication),
                request.role().toUserRole());

        return new LoginApiResponse(
                "Rol actualizado correctamente.",
                result.sessionExpiresAt(),
                UserApiResponse.from(result.user()));
    }

    private String extractSessionToken(Authentication authentication) {
        if (authentication == null || authentication.getCredentials() == null) {
            return userService.getAuthenticatedSessionToken();
        }

        return authentication.getCredentials().toString();
    }
}

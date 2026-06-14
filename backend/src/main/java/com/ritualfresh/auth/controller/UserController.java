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
import com.ritualfresh.auth.dto.RegisterUserRole;
import com.ritualfresh.auth.dto.UserApiResponse;
import com.ritualfresh.auth.model.User;
import com.ritualfresh.auth.service.UserService;
import com.ritualfresh.shared.exception.BusinessRuleException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterUserApiResponse registerUser(@Valid @RequestBody RegisterUserApiRequest request) {
        RegisterUserResult result = userService.registerUser(new RegisterUserRequest(
                request.firstName(),
                request.lastName(),
                request.documentNumber(),
                request.phoneNumber(),
                request.email(),
                request.password(),
                request.confirmPassword(),
                request.role().toUserRole()));

        return new RegisterUserApiResponse(
                result.message(),
                result.accountValidationToken(),
                UserApiResponse.from(result.user()));
    }

    @GetMapping("/validation")
    public AccountValidationApiResponse validateAccount(@RequestParam String token) {
        User user = userService.validateAccount(token);

        return new AccountValidationApiResponse(
                "Cuenta validada correctamente.",
                UserApiResponse.from(user));
    }

    @PostMapping("/login")
    public LoginApiResponse login(@Valid @RequestBody LoginApiRequest request) {
        LoginResult result = userService.login(new LoginRequest(
                request.email(),
                request.password()));

        return new LoginApiResponse(
                "Login de sesion exitoso.",
                result.sessionToken(),
                result.sessionExpiresAt(),
                UserApiResponse.from(result.user()));
    }

    @PostMapping("/password-reset")
    public PasswordResetApiResponse requestPasswordReset(@Valid @RequestBody PasswordResetApiRequest request) {
        PasswordResetResult result = userService.requestPasswordReset(
                new PasswordResetRequest(request.email()));

        return new PasswordResetApiResponse(
                result.message(),
                result.resetToken(),
                result.expiresAt());
    }

    @PostMapping("/password-reset/confirm")
    public MessageApiResponse confirmPasswordReset(@Valid @RequestBody ConfirmPasswordResetApiRequest request) {
        userService.confirmPasswordReset(new ConfirmPasswordResetRequest(
                request.resetToken(),
                request.password(),
                request.confirmPassword()));

        return new MessageApiResponse("Password actualizada correctamente.");
    }

    @PostMapping("/logout")
    public MessageApiResponse closeSession(@RequestHeader("Authorization") String authorization) {
        userService.closeSession(extractSessionToken(authorization));

        return new MessageApiResponse("Session cerrada correctamente.");
    }

    private String extractSessionToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new BusinessRuleException("Debe iniciar sesion para acceder a esta funcionalidad.");
        }

        return authorization.substring("Bearer ".length()).trim();
    }
}

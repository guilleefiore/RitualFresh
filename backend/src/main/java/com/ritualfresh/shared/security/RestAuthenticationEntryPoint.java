package com.ritualfresh.shared.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ritualfresh.shared.exception.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final String DEFAULT_MESSAGE = "Debe iniciar sesion para acceder a esta funcionalidad.";

    private final ObjectMapper objectMapper;

    public RestAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(
                response.getOutputStream(),
                new ApiErrorResponse(HttpStatus.UNAUTHORIZED.value(), resolveMessage(authException)));
    }

    private String resolveMessage(AuthenticationException authException) {
        if (authException == null || authException.getMessage() == null || authException.getMessage().isBlank()) {
            return DEFAULT_MESSAGE;
        }

        if ("Full authentication is required to access this resource".equals(authException.getMessage())) {
            return DEFAULT_MESSAGE;
        }

        return authException.getMessage();
    }
}

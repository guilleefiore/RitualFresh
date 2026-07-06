package com.ritualfresh.shared.security;

import com.ritualfresh.auth.dto.OAuth2ProfileData;
import com.ritualfresh.auth.dto.LoginResult;
import com.ritualfresh.auth.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Map;

public class GoogleOAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private static final Logger log = LoggerFactory.getLogger(GoogleOAuth2SuccessHandler.class);
    private final UserService userService;
    private final String frontendBaseUrl;

    public GoogleOAuth2SuccessHandler(
            UserService userService,
            @Value("${ritualfresh.auth.oauth2-frontend-base-url:http://localhost:5173}") String frontendBaseUrl) {
        this.userService = userService;
        this.frontendBaseUrl = frontendBaseUrl;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        try {
            OAuth2ProfileData profileData = extractProfileData(authentication);
            LoginResult result = userService.loginWithGoogle(profileData);

            response.addHeader(
                    "Set-Cookie",
                    SessionCookieUtils.buildSessionCookieHeader(result.sessionToken(), result.sessionExpiresAt()));
            String target = result.isNewUser()
                    ? trimTrailingSlash(frontendBaseUrl) + "/choose-role"
                    : redirectTargetFor(result.user().getRole().name());
            response.sendRedirect(target);
        } catch (RuntimeException exception) {
            log.error("Error en login con Google: {}", exception.getMessage(), exception);
            response.sendRedirect(trimTrailingSlash(frontendBaseUrl) + "/login?oauth=error");
        }
    }

    private OAuth2ProfileData extractProfileData(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof OAuth2User oauth2User)) {
            throw new IllegalStateException("No se pudo leer el perfil de Google.");
        }

        Map<String, Object> attributes = oauth2User.getAttributes();
        String email = firstNonBlank(
                attributeAsString(attributes, "email"),
                oauth2User instanceof OidcUser oidcUser ? oidcUser.getEmail() : null);
        String firstName = firstNonBlank(
                attributeAsString(attributes, "given_name"),
                attributeAsString(attributes, "name"));
        String lastName = attributeAsString(attributes, "family_name");

        return new OAuth2ProfileData(email, firstName, lastName);
    }

    private String redirectTargetFor(String roleName) {
        String baseUrl = trimTrailingSlash(frontendBaseUrl);

        String path = switch (roleName) {
            case "ADMIN" -> "/admin/home";
            case "WORKER" -> "/worker/home";
            default -> "/client/home";
        };

        return baseUrl + path;
    }

    private String trimTrailingSlash(String value) {
        if (value == null || value.isBlank()) {
            return "http://localhost:5173";
        }

        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    private String attributeAsString(Map<String, Object> attributes, String key) {
        Object value = attributes.get(key);
        return value == null ? null : value.toString();
    }

    private String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }

        if (second != null && !second.isBlank()) {
            return second;
        }

        return null;
    }
}

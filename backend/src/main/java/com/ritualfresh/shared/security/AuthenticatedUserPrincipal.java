package com.ritualfresh.shared.security;

import com.ritualfresh.auth.model.User;
import lombok.Getter;

@Getter
public class AuthenticatedUserPrincipal {
    private final Long userId;
    private final String email;
    private final String role;

    private AuthenticatedUserPrincipal(Long userId, String email, String role) {
        this.userId = userId;
        this.email = email;
        this.role = role;
    }

    public static AuthenticatedUserPrincipal from(User user) {
        return new AuthenticatedUserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getRole().name());
    }
}

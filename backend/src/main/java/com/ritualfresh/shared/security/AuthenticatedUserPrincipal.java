package com.ritualfresh.shared.security;

import com.ritualfresh.auth.model.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthenticatedUserPrincipal {
    private final Long userId;
    private final String email;
    private final String role;

    public static AuthenticatedUserPrincipal from(User user) {
        return new AuthenticatedUserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getRole().name());
    }
}

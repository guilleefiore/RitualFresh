package com.ritualfresh.shared.security;

import com.ritualfresh.auth.model.User;
import com.ritualfresh.auth.model.UserSession;
import com.ritualfresh.auth.repository.UserSessionRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SessionAuthenticationFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";

    private final UserSessionRepository userSessionRepository;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String sessionToken = resolveSessionToken(request, response);
        if (sessionToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            UserSession session = userSessionRepository.findByToken(sessionToken)
                    .orElseThrow(() -> new BadCredentialsException("Debe iniciar sesion para acceder a esta funcionalidad."));

            if (!session.isActive(LocalDateTime.now())) {
                throw new CredentialsExpiredException("La sesion expiro. Debe iniciar sesion nuevamente.");
            }

            User user = session.getUser();
            if (!user.isActive()) {
                throw new BadCredentialsException("La cuenta no se encuentra activa.");
            }

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    AuthenticatedUserPrincipal.from(user),
                    sessionToken,
                    List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (org.springframework.security.core.AuthenticationException exception) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, exception);
        }
    }

    private String resolveSessionToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && !authorization.isBlank()) {
            if (!authorization.startsWith(BEARER_PREFIX)) {
                authenticationEntryPoint.commence(
                        request,
                        response,
                        new InsufficientAuthenticationException("Debe iniciar sesion para acceder a esta funcionalidad."));
                return null;
            }

            String bearerToken = authorization.substring(BEARER_PREFIX.length()).trim();
            if (bearerToken.isBlank()) {
                authenticationEntryPoint.commence(
                        request,
                        response,
                        new InsufficientAuthenticationException("Debe iniciar sesion para acceder a esta funcionalidad."));
                return null;
            }

            return bearerToken;
        }

        return SessionCookieUtils.resolveSessionTokenFromCookie(request);
    }
}

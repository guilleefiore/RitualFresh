package com.ritualfresh.notifications;

import com.ritualfresh.auth.dto.LoginResult;
import com.ritualfresh.auth.model.User;
import com.ritualfresh.auth.model.UserRole;
import com.ritualfresh.auth.service.UserService;
import com.ritualfresh.notifications.websocket.NotificationHandshakeInterceptor;
import com.ritualfresh.notifications.websocket.NotificationWebSocketHandler;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.socket.WebSocketHandler;

import java.time.LocalDateTime;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NotificationHandshakeInterceptorTest {
    @Test
    void authenticatesWebSocketWithSessionCookie() {
        UserService userService = mock(UserService.class);
        User user = activeUser();
        when(userService.getAuthenticatedSession("valid-token"))
                .thenReturn(new LoginResult(user, "valid-token", LocalDateTime.now().plusHours(1)));
        NotificationHandshakeInterceptor interceptor = new NotificationHandshakeInterceptor(userService);
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setCookies(new Cookie("RITUALFRESH_SESSION", "valid-token"));
        var attributes = new HashMap<String, Object>();

        boolean accepted = interceptor.beforeHandshake(
                new ServletServerHttpRequest(servletRequest),
                new ServletWebSocketResponse(),
                mock(WebSocketHandler.class),
                attributes);

        assertTrue(accepted);
        assertEquals(user.getId(), attributes.get(NotificationWebSocketHandler.USER_ID_ATTRIBUTE));
    }

    @Test
    void rejectsWebSocketWithoutSessionCookie() {
        NotificationHandshakeInterceptor interceptor = new NotificationHandshakeInterceptor(mock(UserService.class));

        boolean accepted = interceptor.beforeHandshake(
                new ServletServerHttpRequest(new MockHttpServletRequest()),
                new ServletWebSocketResponse(),
                mock(WebSocketHandler.class),
                new HashMap<>());

        assertFalse(accepted);
    }

    @Test
    void rejectsWebSocketWithInvalidSessionCookie() {
        UserService userService = mock(UserService.class);
        when(userService.getAuthenticatedSession("invalid-token"))
                .thenThrow(new com.ritualfresh.shared.exception.BusinessRuleException("Sesión inválida."));
        NotificationHandshakeInterceptor interceptor = new NotificationHandshakeInterceptor(userService);
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setCookies(new Cookie("RITUALFRESH_SESSION", "invalid-token"));
        ServletWebSocketResponse response = new ServletWebSocketResponse();

        boolean accepted = interceptor.beforeHandshake(
                new ServletServerHttpRequest(servletRequest),
                response,
                mock(WebSocketHandler.class),
                new HashMap<>());

        assertFalse(accepted);
        assertEquals(org.springframework.http.HttpStatus.UNAUTHORIZED, response.statusCode);
    }

    private User activeUser() {
        User user = User.register(new User.RegistrationData(
                "Socket",
                "User",
                "socket.notifications@example.com",
                "hash",
                UserRole.CLIENT,
                LocalDateTime.now(),
                "validation-socket",
                LocalDateTime.now().plusDays(1)));
        user.validateAccount();
        user.assignIdIfMissing(99L);
        return user;
    }

    private static final class ServletWebSocketResponse implements org.springframework.http.server.ServerHttpResponse {
        private org.springframework.http.HttpStatusCode statusCode;

        @Override
        public void setStatusCode(org.springframework.http.HttpStatusCode status) {
            this.statusCode = status;
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }

        @Override
        public org.springframework.http.HttpHeaders getHeaders() {
            return new org.springframework.http.HttpHeaders();
        }

        @Override
        public java.io.OutputStream getBody() {
            return java.io.OutputStream.nullOutputStream();
        }
    }
}

package com.ritualfresh.notifications.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler {
    public static final String USER_ID_ATTRIBUTE = "notificationUserId";

    private final NotificationWebSocketHub hub;

    public NotificationWebSocketHandler(NotificationWebSocketHub hub) {
        this.hub = hub;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long userId = (Long) session.getAttributes().get(USER_ID_ATTRIBUTE);
        if (userId != null) {
            hub.register(userId, session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = (Long) session.getAttributes().get(USER_ID_ATTRIBUTE);
        if (userId != null) {
            hub.unregister(userId, session);
        }
    }
}

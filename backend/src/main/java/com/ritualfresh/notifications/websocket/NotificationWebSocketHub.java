package com.ritualfresh.notifications.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ritualfresh.notifications.realtime.NotificationRealtimePublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NotificationWebSocketHub implements NotificationRealtimePublisher {
    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<Long, Set<WebSocketSession>> sessionsByUserId = new ConcurrentHashMap<>();

    public NotificationWebSocketHub(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void register(Long userId, WebSocketSession session) {
        sessionsByUserId.computeIfAbsent(userId, ignored -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void unregister(Long userId, WebSocketSession session) {
        Set<WebSocketSession> sessions = sessionsByUserId.get(userId);
        if (sessions == null) {
            return;
        }
        sessions.remove(session);
        if (sessions.isEmpty()) {
            sessionsByUserId.remove(userId);
        }
    }

    @Override
    public void publish(Long recipientId, String type, Object payload) {
        Set<WebSocketSession> sessions = sessionsByUserId.get(recipientId);
        if (sessions == null || sessions.isEmpty()) {
            return;
        }

        NotificationRealtimeEvent event = new NotificationRealtimeEvent(type, payload);
        sessions.removeIf(session -> !send(session, event));
    }

    private boolean send(WebSocketSession session, NotificationRealtimeEvent event) {
        if (!session.isOpen()) {
            return false;
        }
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(event)));
            return true;
        } catch (IOException exception) {
            return false;
        }
    }
}

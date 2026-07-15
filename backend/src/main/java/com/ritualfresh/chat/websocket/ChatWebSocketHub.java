package com.ritualfresh.chat.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHub {
    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<Long, Set<WebSocketSession>> sessionsByUserId = new ConcurrentHashMap<>();

    public ChatWebSocketHub(ObjectMapper objectMapper) {
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

    public void sendToUser(Long userId, String type, Object payload) {
        Set<WebSocketSession> sessions = sessionsByUserId.get(userId);
        if (sessions == null || sessions.isEmpty()) {
            return;
        }

        ChatRealtimeEvent event = new ChatRealtimeEvent(type, payload);
        sessions.removeIf(session -> !send(session, event));
    }

    public void sendToUsers(Long firstUserId, Long secondUserId, String type, Object payload) {
        sendToUser(firstUserId, type, payload);
        if (!firstUserId.equals(secondUserId)) {
            sendToUser(secondUserId, type, payload);
        }
    }

    private boolean send(WebSocketSession session, ChatRealtimeEvent event) {
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

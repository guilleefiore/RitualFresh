package com.ritualfresh.notifications;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ritualfresh.notifications.websocket.NotificationWebSocketHub;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificationWebSocketHubTest {
    @Test
    void publishesOnlyToSessionsOwnedByRecipient() throws IOException {
        NotificationWebSocketHub hub = new NotificationWebSocketHub(new ObjectMapper());
        WebSocketSession recipientSession = mock(WebSocketSession.class);
        WebSocketSession otherSession = mock(WebSocketSession.class);
        when(recipientSession.isOpen()).thenReturn(true);
        when(otherSession.isOpen()).thenReturn(true);
        hub.register(10L, recipientSession);
        hub.register(20L, otherSession);

        hub.publish(10L, "notification.created", Map.of("unreadCount", 1));

        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(recipientSession).sendMessage(messageCaptor.capture());
        verify(otherSession, never()).sendMessage(org.mockito.ArgumentMatchers.any());
        assertTrue(messageCaptor.getValue().getPayload().contains("notification.created"));
    }
}

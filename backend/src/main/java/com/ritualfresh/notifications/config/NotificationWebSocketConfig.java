package com.ritualfresh.notifications.config;

import com.ritualfresh.notifications.websocket.NotificationHandshakeInterceptor;
import com.ritualfresh.notifications.websocket.NotificationWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class NotificationWebSocketConfig implements WebSocketConfigurer {
    private final NotificationWebSocketHandler handler;
    private final NotificationHandshakeInterceptor handshakeInterceptor;

    public NotificationWebSocketConfig(
            NotificationWebSocketHandler handler,
            NotificationHandshakeInterceptor handshakeInterceptor) {
        this.handler = handler;
        this.handshakeInterceptor = handshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws/notifications")
                .addInterceptors(handshakeInterceptor)
                .setAllowedOrigins("http://localhost:5173", "http://127.0.0.1:5173");
    }
}

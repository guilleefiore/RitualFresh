package com.ritualfresh.chat.dto;

import com.ritualfresh.chat.model.ChatMessage;

import java.time.LocalDateTime;

public record MessageApiResponse(
        Long id,
        Long conversationId,
        Long senderId,
        String content,
        LocalDateTime createdAt,
        LocalDateTime readAt,
        String clientMessageId) {
    public static MessageApiResponse from(ChatMessage message) {
        return from(message, null);
    }

    public static MessageApiResponse from(ChatMessage message, String clientMessageId) {
        return new MessageApiResponse(
                message.getId(),
                message.getConversation().getId(),
                message.getSender().getId(),
                message.getContent(),
                message.getCreatedAt(),
                message.getReadAt(),
                clientMessageId);
    }
}

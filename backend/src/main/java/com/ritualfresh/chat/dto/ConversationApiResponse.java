package com.ritualfresh.chat.dto;

import com.ritualfresh.auth.model.User;
import com.ritualfresh.chat.model.Conversation;

import java.time.LocalDateTime;

public record ConversationApiResponse(
        Long id,
        String status,
        ChatParticipantApiResponse otherParticipant,
        LocalDateTime lastMessageAt,
        long unreadCount,
        PresenceApiResponse presence) {
    public static ConversationApiResponse from(
            Conversation conversation,
            User currentUser,
            long unreadCount,
            PresenceApiResponse presence) {
        return new ConversationApiResponse(
                conversation.getId(),
                conversation.getStatus().name(),
                ChatParticipantApiResponse.from(conversation.otherParticipant(currentUser.getId())),
                conversation.getLastMessageAt(),
                unreadCount,
                presence);
    }
}

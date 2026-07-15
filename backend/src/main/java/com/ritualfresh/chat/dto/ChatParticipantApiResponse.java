package com.ritualfresh.chat.dto;

import com.ritualfresh.auth.model.User;

public record ChatParticipantApiResponse(
        Long id,
        String firstName,
        String lastName,
        String role) {
    public static ChatParticipantApiResponse from(User user) {
        return new ChatParticipantApiResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name());
    }
}

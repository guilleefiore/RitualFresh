package com.ritualfresh.chat.dto;

import jakarta.validation.constraints.NotNull;

public record CreateConversationApiRequest(@NotNull Long otherUserId) {
}

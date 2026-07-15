package com.ritualfresh.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SendMessageApiRequest(
        @NotBlank @Size(max = 500) String content,
        String clientMessageId) {
}

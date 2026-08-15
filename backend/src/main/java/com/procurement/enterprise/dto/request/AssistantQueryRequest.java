package com.procurement.enterprise.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;
import java.util.List;

/** Request accepted by the controlled procurement assistant. */
public record AssistantQueryRequest(
        @NotBlank(message = "A question is required.")
        @Size(max = 500, message = "Questions must not exceed 500 characters.")
        String question,
        @Size(max = 12, message = "Conversation history is limited to 12 messages.")
        List<@Valid AssistantHistoryMessage> history
) {
}

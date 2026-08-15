package com.procurement.enterprise.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** A small, client supplied context window. It is never treated as identity or authorization data. */
public record AssistantHistoryMessage(
        @NotBlank @Pattern(regexp = "user|assistant", message = "History role must be user or assistant.") String role,
        @NotBlank @Size(max = 500, message = "History messages must not exceed 500 characters.") String content) { }

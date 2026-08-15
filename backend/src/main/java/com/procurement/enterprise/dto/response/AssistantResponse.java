package com.procurement.enterprise.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AssistantResponse {
    private String intent;
    private String message;
    private List<String> suggestions;
}

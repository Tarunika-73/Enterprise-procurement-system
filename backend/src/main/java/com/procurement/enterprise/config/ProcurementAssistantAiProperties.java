package com.procurement.enterprise.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Environment-backed local-provider settings. No API key is used or accepted. */
@Component
public class ProcurementAssistantAiProperties {
    private final boolean enabled;
    private final String provider;
    private final String model;
    private final String ollamaBaseUrl;

    public ProcurementAssistantAiProperties(
            @Value("${PROCUREMENT_ASSISTANT_AI_ENABLED:true}") boolean enabled,
            @Value("${PROCUREMENT_ASSISTANT_AI_PROVIDER:ollama}") String provider,
            @Value("${PROCUREMENT_ASSISTANT_AI_MODEL:llama3.2:3b}") String model,
            @Value("${OLLAMA_BASE_URL:http://localhost:11434}") String ollamaBaseUrl) {
        this.enabled = enabled; this.provider = provider; this.model = model; this.ollamaBaseUrl = ollamaBaseUrl;
    }
    public boolean enabled() { return enabled; }
    public String provider() { return provider; }
    public String model() { return model; }
    public String ollamaBaseUrl() { return ollamaBaseUrl; }
    public boolean isConfigured() {
        return enabled && "ollama".equalsIgnoreCase(provider) && model != null && !model.isBlank()
                && ollamaBaseUrl != null && ollamaBaseUrl.matches("https?://localhost(:\\d+)?/?");
    }
}

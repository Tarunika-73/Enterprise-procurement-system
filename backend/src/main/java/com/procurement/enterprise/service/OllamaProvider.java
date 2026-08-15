package com.procurement.enterprise.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.procurement.enterprise.config.ProcurementAssistantAiProperties;
import com.procurement.enterprise.dto.request.AssistantHistoryMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.*;

/** Local Ollama /api/chat adapter. It has no database dependencies and never receives credentials or tokens. */
@Service
@RequiredArgsConstructor
public class OllamaProvider {
    private static final int MAX_TOOL_ROUNDS = 2;
    private final ProcurementAssistantAiProperties properties;
    private final ControlledAssistantToolExecutor toolExecutor;

    public Optional<String> fastAnswer(String question) { return toolExecutor.fastAnswer(question); }

    public Optional<String> answer(String question, List<AssistantHistoryMessage> history) {
        if (!properties.isConfigured()) return Optional.empty();
        try {
            List<Map<String, Object>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", instructions()));
            if (history != null) for (AssistantHistoryMessage item : history.stream().limit(6).toList())
                messages.add(Map.of("role", item.role(), "content", item.content()));
            messages.add(Map.of("role", "user", "content", question));
            for (int round = 0; round < MAX_TOOL_ROUNDS; round++) {
                JsonNode response = chat(messages);
                JsonNode message = response.path("message");
                JsonNode calls = message.path("tool_calls");
                if (!calls.isArray() || calls.isEmpty()) return responseText(message);
                for (JsonNode call : calls) {
                    JsonNode function = call.path("function");
                    Optional<String> output = toolExecutor.execute(function.path("name").asText(), function.path("arguments"));
                    if (output.isEmpty()) return Optional.empty();
                    return output; // Controlled tool responses are already concise; avoid a second local model generation.
                }
            }
        } catch (Exception ignored) { /* A local provider outage must always use the controlled fallback. */ }
        return Optional.empty();
    }

    private JsonNode chat(List<Map<String, Object>> messages) {
        return client().post().uri(properties.ollamaBaseUrl().replaceAll("/$", "") + "/api/chat")
                .contentType(MediaType.APPLICATION_JSON).body(Map.of("model", properties.model(), "messages", messages, "tools", tools(), "stream", false, "options", Map.of("temperature", 0.1, "num_predict", 160)))
                .retrieve().body(JsonNode.class);
    }
    private RestClient client() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3_000); factory.setReadTimeout(20_000);
        return RestClient.builder().requestFactory(factory).build();
    }
    private Optional<String> responseText(JsonNode message) {
        String text = message.path("content").asText("").trim();
        return text.isBlank() ? Optional.empty() : Optional.of(text.length() > 2000 ? text.substring(0, 2000) : text);
    }
    private String instructions() { return "You are a local procurement assistant. Use a listed tool for every company-data answer. Tools are read-only and enforce authorization. Never request, infer, or expose credentials, tokens, passwords, or database data. Do not invent facts. After tool results, answer concisely using only those results. If a result says permission is denied, repeat it exactly."; }
    private List<Map<String, Object>> tools() {
        List<Map<String, Object>> all = new ArrayList<>(); for (String name : toolExecutor.toolsForAuthenticatedRole()) all.add(tool(name)); return all;
    }
    private Map<String, Object> tool(String name) {
        Map<String, Object> parameters = new LinkedHashMap<>(); parameters.put("type", "object"); parameters.put("additionalProperties", false);
        if ("get_my_request_by_number".equals(name)) { parameters.put("properties", Map.of("request_number", Map.of("type", "string"))); parameters.put("required", List.of("request_number")); }
        else if ("get_product_prices".equals(name) || "get_vendor_product_prices".equals(name)) { parameters.put("properties", Map.of("product_name", Map.of("type", "string"))); parameters.put("required", List.of("product_name")); }
        else parameters.put("properties", Map.of());
        return Map.of("type", "function", "function", Map.of("name", name, "description", "Read-only authorized procurement information.", "parameters", parameters));
    }
}

package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.request.AssistantHistoryMessage;
import com.procurement.enterprise.dto.response.AssistantResponse;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/** Selects remote AI only when fully configured; all failures deliberately use the deterministic fallback. */
@Service
@Primary
public class ProcurementAssistantAiService implements ProcurementAssistantService {
    private static final Logger log = LoggerFactory.getLogger(ProcurementAssistantAiService.class);
    private final OllamaProvider provider;
    private final ProcurementAssistantServiceImpl fallback;

    public ProcurementAssistantAiService(OllamaProvider provider, ProcurementAssistantServiceImpl fallback) {
        this.provider = provider;
        this.fallback = fallback;
    }
    @Override public AssistantResponse answer(String question, List<AssistantHistoryMessage> history) {
        long started = System.nanoTime();
        var fast = provider.fastAnswer(question);
        if (fast.isPresent()) { log.debug("Procurement assistant fast path completed in {} ms", (System.nanoTime() - started) / 1_000_000); return reply(fast.get()); }
        var ai = provider.answer(question, history);
        if (ai.isPresent()) { log.debug("Procurement assistant Ollama path completed in {} ms", (System.nanoTime() - started) / 1_000_000); return reply(ai.get()); }
        AssistantResponse response = fallback.answer(question, history);
        log.debug("Procurement assistant fallback completed in {} ms", (System.nanoTime() - started) / 1_000_000);
        return response;
    }
    private AssistantResponse reply(String message) { return AssistantResponse.builder().intent("AI_RESPONSE").message(message).suggestions(List.of()).build(); }
}

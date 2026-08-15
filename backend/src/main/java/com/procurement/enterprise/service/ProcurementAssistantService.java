package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.response.AssistantResponse;
import com.procurement.enterprise.dto.request.AssistantHistoryMessage;
import java.util.List;

public interface ProcurementAssistantService {
    AssistantResponse answer(String question, List<AssistantHistoryMessage> history);
}

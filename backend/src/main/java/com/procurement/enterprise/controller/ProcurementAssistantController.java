package com.procurement.enterprise.controller;

import com.procurement.enterprise.dto.request.AssistantQueryRequest;
import com.procurement.enterprise.dto.response.AssistantResponse;
import com.procurement.enterprise.service.ProcurementAssistantService;
import com.procurement.enterprise.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/procurement-assistant")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'PROCUREMENT_OFFICER', 'VENDOR', 'FINANCE', 'ADMIN')")
public class ProcurementAssistantController {

    private final ProcurementAssistantService procurementAssistantService;

    @PostMapping("/ask")
    public ResponseEntity<ApiResponse<AssistantResponse>> ask(@Valid @RequestBody AssistantQueryRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Assistant response generated", procurementAssistantService.answer(request.question(), request.history())));
    }
}

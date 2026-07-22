package com.procurement.enterprise.controller;

import com.procurement.enterprise.dto.approvalhistory.ApprovalHistoryResponse;
import com.procurement.enterprise.service.ApprovalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approval-history")
@RequiredArgsConstructor
public class ApprovalHistoryController {

    private final ApprovalHistoryService approvalHistoryService;

    /**
     * Get complete approval history of a purchase request.
     *
     * GET /api/approval-history/request/{requestId}
     */
    @GetMapping("/request/{requestId}")
    public ResponseEntity<List<ApprovalHistoryResponse>> getApprovalHistory(
            @PathVariable Long requestId) {

        List<ApprovalHistoryResponse> response =
                approvalHistoryService.getApprovalHistory(requestId);

        return ResponseEntity.ok(response);
    }

    /**
     * Get all approvals performed by an approver.
     *
     * GET /api/approval-history/approver/{approverId}
     */
    @GetMapping("/approver/{approverId}")
    public ResponseEntity<List<ApprovalHistoryResponse>> getHistoryByApprover(
            @PathVariable Long approverId) {

        List<ApprovalHistoryResponse> response =
                approvalHistoryService.getHistoryByApprover(approverId);

        return ResponseEntity.ok(response);
    }
}
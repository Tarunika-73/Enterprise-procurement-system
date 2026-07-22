package com.procurement.enterprise.controller;

import com.procurement.enterprise.dto.approvalworkflow.ApprovalRequest;
import com.procurement.enterprise.dto.approvalworkflow.PendingApprovalResponse;
import com.procurement.enterprise.dto.approvalworkflow.RejectionRequest;
import com.procurement.enterprise.dto.approvalworkflow.WorkflowStatusResponse;
import com.procurement.enterprise.service.ApprovalWorkflowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approval-workflow")
@RequiredArgsConstructor
public class ApprovalWorkflowController {

    private final ApprovalWorkflowService approvalWorkflowService;

    /**
     * Approve a purchase requisition.
     *
     * POST /api/approval-workflow/approve
     */
    @PostMapping("/approve")
    public ResponseEntity<WorkflowStatusResponse> approveRequest(
            @Valid @RequestBody ApprovalRequest request) {

        WorkflowStatusResponse response =
                approvalWorkflowService.approveRequest(request);

        return ResponseEntity.ok(response);
    }

    /**
     * Reject a purchase requisition.
     *
     * POST /api/approval-workflow/reject
     */
    @PostMapping("/reject")
    public ResponseEntity<WorkflowStatusResponse> rejectRequest(
            @Valid @RequestBody RejectionRequest request) {

        WorkflowStatusResponse response =
                approvalWorkflowService.rejectRequest(request);

        return ResponseEntity.ok(response);
    }

    /**
     * Get all requests currently pending for an approver.
     *
     * GET /api/approval-workflow/pending/{approverId}
     */
    @GetMapping("/pending/{approverId}")
    public ResponseEntity<List<PendingApprovalResponse>> getPendingRequests(
            @PathVariable Long approverId) {

        List<PendingApprovalResponse> responses =
                approvalWorkflowService.getPendingRequests(approverId);

        return ResponseEntity.ok(responses);
    }

    /**
     * Get all requests assigned to or previously handled by an approver.
     *
     * GET /api/approval-workflow/approver/{approverId}
     */
    @GetMapping("/approver/{approverId}")
    public ResponseEntity<List<PendingApprovalResponse>> getRequestsByApprover(
            @PathVariable Long approverId) {

        List<PendingApprovalResponse> responses =
                approvalWorkflowService.getRequestsByApprover(approverId);

        return ResponseEntity.ok(responses);
    }

    /**
     * Get the current workflow status of a purchase requisition.
     *
     * GET /api/approval-workflow/status/{requestId}
     */
    @GetMapping("/status/{requestId}")
    public ResponseEntity<WorkflowStatusResponse> getWorkflowStatus(
            @PathVariable Long requestId) {

        WorkflowStatusResponse response =
                approvalWorkflowService.getWorkflowStatus(requestId);

        return ResponseEntity.ok(response);
    }

    /**
     * Optional endpoint to check whether the controller is running.
     *
     * GET /api/approval-workflow/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Approval Workflow service is running");
    }
}
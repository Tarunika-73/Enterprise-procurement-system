
package com.procurement.enterprise.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.procurement.enterprise.dto.request.ApprovalActionRequest;
import com.procurement.enterprise.dto.request.CreateApprovalRequest;
import com.procurement.enterprise.dto.request.UpdateApprovalRequest;
import com.procurement.enterprise.dto.response.ApprovalHistoryResponse;
import com.procurement.enterprise.dto.response.ApprovalResponse;
import com.procurement.enterprise.enums.ApprovalStatus;
import com.procurement.enterprise.service.ApprovalService;
import com.procurement.enterprise.util.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST controller for approval hierarchy management: CRUD on multi-level
 * approval steps for purchase requests, decision actions (approve / reject /
 * escalate), and approval-history lookups.
 */
@RestController
@RequestMapping({"/v1/approvals", "/approvals"})
@RequiredArgsConstructor
public class ApprovalController {

    private static final Logger log = LoggerFactory.getLogger(ApprovalController.class);

    private final ApprovalService approvalService;

    @PostMapping
    public ResponseEntity<ApiResponse<ApprovalResponse>> createApproval(
            @Valid @RequestBody CreateApprovalRequest request) {

        log.info("Creating approval step for purchase request id: {}, level: {}",
                request.getPurchaseRequestId(), request.getLevel());

        ApprovalResponse response = approvalService.createApproval(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Approval step created successfully", response, HttpStatus.CREATED));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ApprovalResponse>>> getAllApprovals(
            @ParameterObject Pageable pageable,
            @RequestParam(required = false) Long purchaseRequestId,
            @RequestParam(required = false) Long approverId,
            @RequestParam(required = false) ApprovalStatus status) {

        log.info("Fetching approvals (purchaseRequestId={}, approverId={}, status={})",
                purchaseRequestId, approverId, status);

        Page<ApprovalResponse> approvals;
        if (purchaseRequestId != null) {
            approvals = approvalService.getApprovalsByPurchaseRequest(purchaseRequestId, pageable);
        } else if (approverId != null) {
            approvals = approvalService.getApprovalsByApprover(approverId, pageable);
        } else if (status != null) {
            approvals = approvalService.getApprovalsByStatus(status, pageable);
        } else {
            approvals = approvalService.getAllApprovals(pageable);
        }

        return ResponseEntity.ok(ApiResponse.success("Approvals fetched successfully", approvals));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApprovalResponse>> getApprovalById(@PathVariable Long id) {

        log.info("Fetching approval with id: {}", id);

        ApprovalResponse response = approvalService.getApprovalById(id);

        return ResponseEntity.ok(ApiResponse.success("Approval fetched successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ApprovalResponse>> updateApproval(
            @PathVariable Long id,
            @Valid @RequestBody UpdateApprovalRequest request) {

        log.info("Updating approval with id: {}", id);

        ApprovalResponse response = approvalService.updateApproval(id, request);

        return ResponseEntity.ok(ApiResponse.success("Approval updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteApproval(@PathVariable Long id) {

        log.info("Deleting approval with id: {}", id);

        approvalService.deleteApproval(id);

        return ResponseEntity.ok(ApiResponse.success("Approval deleted successfully", null));
    }

    @PostMapping("/{id}/actions")
    public ResponseEntity<ApiResponse<ApprovalResponse>> takeAction(
            @PathVariable Long id,
            @Valid @RequestBody ApprovalActionRequest request) {

        log.info("Actioning approval id: {} with decision: {}", id, request.getActionTaken());

        ApprovalResponse response = approvalService.takeAction(id, request);

        return ResponseEntity.ok(ApiResponse.success("Approval action recorded successfully", response));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<ApiResponse<Page<ApprovalHistoryResponse>>> getApprovalHistory(
            @PathVariable Long id,
            @ParameterObject Pageable pageable) {

        log.info("Fetching approval history for approval id: {}", id);

        Page<ApprovalHistoryResponse> history = approvalService.getApprovalHistory(id, pageable);

        return ResponseEntity.ok(ApiResponse.success("Approval history fetched successfully", history));
    }
}

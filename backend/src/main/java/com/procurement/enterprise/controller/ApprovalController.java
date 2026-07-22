package com.procurement.enterprise.controller;

import com.procurement.enterprise.dto.request.CreateApprovalRequest;
import com.procurement.enterprise.dto.request.UpdateApprovalRequest;
import com.procurement.enterprise.dto.response.ApprovalResponse;
import com.procurement.enterprise.enums.ApprovalStatus;
import com.procurement.enterprise.service.ApprovalService;
import com.procurement.enterprise.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/v1/approval", "/approval"})
@RequiredArgsConstructor
public class ApprovalController {

    private static final Logger log =
            LoggerFactory.getLogger(ApprovalController.class);

    private final ApprovalService approvalService;

    @PostMapping
    public ResponseEntity<ApiResponse<ApprovalResponse>> create(
            @Valid @RequestBody CreateApprovalRequest request) {

        log.info("Creating approval for purchase request {}",
                request.getPurchaseRequestId());

        ApprovalResponse response = approvalService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Approval created successfully",
                        response,
                        HttpStatus.CREATED
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ApprovalResponse>>> getAll(
            @ParameterObject Pageable pageable) {

        Page<ApprovalResponse> response =
                approvalService.getAll(pageable);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Approvals fetched successfully",
                        response
                ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApprovalResponse>> getById(
            @PathVariable Long id) {

        ApprovalResponse response = approvalService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Approval fetched successfully",
                        response
                ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ApprovalResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateApprovalRequest request) {

        ApprovalResponse response = approvalService.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Approval updated successfully",
                        response
                ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id) {

        approvalService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Approval deleted successfully",
                        null
                ));
    }

    @GetMapping("/purchase-request/{purchaseRequestId}")
    public ResponseEntity<ApiResponse<Page<ApprovalResponse>>> getByPurchaseRequest(
            @PathVariable Long purchaseRequestId,
            @ParameterObject Pageable pageable) {

        Page<ApprovalResponse> response =
                approvalService.getByPurchaseRequest(
                        purchaseRequestId,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Approvals fetched successfully",
                        response
                ));
    }

    @GetMapping("/approver/{approverId}")
    public ResponseEntity<ApiResponse<Page<ApprovalResponse>>> getByApprover(
            @PathVariable Long approverId,
            @ParameterObject Pageable pageable) {

        Page<ApprovalResponse> response =
                approvalService.getByApprover(approverId, pageable);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Approvals fetched successfully",
                        response
                ));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<Page<ApprovalResponse>>> getByStatus(
            @PathVariable ApprovalStatus status,
            @ParameterObject Pageable pageable) {

        Page<ApprovalResponse> response =
                approvalService.getByStatus(status, pageable);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Approvals fetched successfully",
                        response
                ));
    }

    @GetMapping("/approver/{approverId}/status/{status}")
    public ResponseEntity<ApiResponse<Page<ApprovalResponse>>> getByApproverAndStatus(
            @PathVariable Long approverId,
            @PathVariable ApprovalStatus status,
            @ParameterObject Pageable pageable) {

        Page<ApprovalResponse> response =
                approvalService.getByApproverAndStatus(
                        approverId,
                        status,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Approvals fetched successfully",
                        response
                ));
    }
}

package com.procurement.enterprise.controller;

import com.procurement.enterprise.dto.approvalhierarchy.ApprovalHierarchyRequest;
import com.procurement.enterprise.dto.approvalhierarchy.ApprovalHierarchyResponse;
import com.procurement.enterprise.service.ApprovalHierarchyService;
import com.procurement.enterprise.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Approval Hierarchy Management.
 *
 * Lets an admin/manager define the ordered chain of approver roles a
 * purchase requisition must pass through, per department and requisition
 * amount range. This configuration drives:
 *  - Automatic Request Routing (who gets the requisition first)
 *  - The Multi-Level Approval Workflow (who is next after each approval)
 *
 * @see com.procurement.enterprise.service.ApprovalRoutingService
 */
@RestController
@RequestMapping({"/v1/approval-hierarchy", "/approval-hierarchy"})
@RequiredArgsConstructor
public class ApprovalHierarchyController {

    private final ApprovalHierarchyService approvalHierarchyService;

    /**
     * Define a new multi-level approval hierarchy.
     * POST /v1/approval-hierarchy
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ApprovalHierarchyResponse>> create(
            @Valid @RequestBody ApprovalHierarchyRequest request) {

        ApprovalHierarchyResponse response = approvalHierarchyService.createHierarchy(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Approval hierarchy created successfully.", response));
    }

    /**
     * Update an existing approval hierarchy (replaces its level chain).
     * PUT /v1/approval-hierarchy/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ApprovalHierarchyResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ApprovalHierarchyRequest request) {

        ApprovalHierarchyResponse response = approvalHierarchyService.updateHierarchy(id, request);

        return ResponseEntity.ok(
                ApiResponse.success("Approval hierarchy updated successfully.", response)
        );
    }

    /**
     * GET /v1/approval-hierarchy/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApprovalHierarchyResponse>> getById(@PathVariable Long id) {

        ApprovalHierarchyResponse response = approvalHierarchyService.getHierarchyById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Approval hierarchy fetched successfully.", response)
        );
    }

    /**
     * GET /v1/approval-hierarchy
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ApprovalHierarchyResponse>>> getAll(
            @ParameterObject Pageable pageable) {

        Page<ApprovalHierarchyResponse> response = approvalHierarchyService.getAllHierarchies(pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Approval hierarchies fetched successfully.", response)
        );
    }

    /**
     * Soft-deletes an approval hierarchy.
     * DELETE /v1/approval-hierarchy/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        approvalHierarchyService.deleteHierarchy(id);

        return ResponseEntity.ok(
                ApiResponse.success("Approval hierarchy deleted successfully.", null)
        );
    }

    /**
     * Preview which hierarchy a requisition for the given department and
     * amount would be routed through, before it is actually submitted.
     *
     * GET /v1/approval-hierarchy/resolve?departmentId=1&amount=50000
     */
    @GetMapping("/resolve")
    public ResponseEntity<ApiResponse<ApprovalHierarchyResponse>> resolve(
            @RequestParam Long departmentId,
            @RequestParam BigDecimal amount) {

        ApprovalHierarchyResponse response =
                approvalHierarchyService.resolveApplicableHierarchy(departmentId, amount);

        return ResponseEntity.ok(
                ApiResponse.success("Applicable approval hierarchy resolved successfully.", response)
        );
    }
}

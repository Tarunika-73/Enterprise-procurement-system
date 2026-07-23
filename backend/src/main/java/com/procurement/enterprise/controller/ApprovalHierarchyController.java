package com.procurement.enterprise.controller;

import com.procurement.enterprise.dto.request.CreateApprovalHierarchyRequest;
import com.procurement.enterprise.dto.request.UpdateApprovalHierarchyRequest;
import com.procurement.enterprise.dto.response.ApprovalHierarchyResponse;
import com.procurement.enterprise.service.ApprovalHierarchyService;
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

import java.util.List;

/**
 * CRUD endpoints for configuring per-department approval hierarchies
 * (e.g. Department -> Manager -> Finance -> Procurement Head).
 */
@RestController
@RequestMapping({"/v1/approval-hierarchies", "/approval-hierarchies"})
@RequiredArgsConstructor
public class ApprovalHierarchyController {

    private static final Logger log =
            LoggerFactory.getLogger(ApprovalHierarchyController.class);

    private final ApprovalHierarchyService approvalHierarchyService;

    @PostMapping
    public ResponseEntity<ApiResponse<ApprovalHierarchyResponse>> createHierarchy(
            @Valid @RequestBody CreateApprovalHierarchyRequest request) {

        log.info("Creating approval hierarchy level {} for department {}",
                request.getLevel(), request.getDepartmentId());

        ApprovalHierarchyResponse response = approvalHierarchyService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Approval hierarchy level created successfully",
                        response,
                        HttpStatus.CREATED));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ApprovalHierarchyResponse>>> getAllHierarchies(
            @ParameterObject Pageable pageable) {

        Page<ApprovalHierarchyResponse> response = approvalHierarchyService.getAll(pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Approval hierarchies fetched successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApprovalHierarchyResponse>> getHierarchyById(
            @PathVariable Long id) {

        ApprovalHierarchyResponse response = approvalHierarchyService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Approval hierarchy fetched successfully", response));
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<ApiResponse<List<ApprovalHierarchyResponse>>> getHierarchyByDepartment(
            @PathVariable Long departmentId) {

        List<ApprovalHierarchyResponse> response =
                approvalHierarchyService.getByDepartment(departmentId);

        return ResponseEntity.ok(
                ApiResponse.success("Department approval hierarchy fetched successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ApprovalHierarchyResponse>> updateHierarchy(
            @PathVariable Long id,
            @Valid @RequestBody UpdateApprovalHierarchyRequest request) {

        ApprovalHierarchyResponse response = approvalHierarchyService.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.success("Approval hierarchy updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteHierarchy(@PathVariable Long id) {

        approvalHierarchyService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.success("Approval hierarchy deleted successfully", null));
    }
}

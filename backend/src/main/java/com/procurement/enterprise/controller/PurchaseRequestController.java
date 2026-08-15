package com.procurement.enterprise.controller;

import com.procurement.enterprise.dto.request.CreatePurchaseRequestRequest;
import com.procurement.enterprise.dto.request.ManagerDecisionRequest;
import com.procurement.enterprise.dto.response.EmployeeDashboardStatsResponse;
import com.procurement.enterprise.dto.response.ManagerDashboardStatsResponse;
import com.procurement.enterprise.dto.response.PurchaseRequestResponse;
import com.procurement.enterprise.service.PurchaseRequestService;
import com.procurement.enterprise.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/v1/purchase-requests", "/purchase-requests"})
@RequiredArgsConstructor
public class PurchaseRequestController {

    private static final Logger log = LoggerFactory.getLogger(PurchaseRequestController.class);

    private final PurchaseRequestService purchaseRequestService;

    @PostMapping
    public ResponseEntity<ApiResponse<PurchaseRequestResponse>> create(
            @Valid @RequestBody CreatePurchaseRequestRequest request) {

        log.info("Creating purchase request for product {}", request.getProductId());
        PurchaseRequestResponse response = purchaseRequestService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Purchase request created successfully.",
                        response,
                        HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<PurchaseRequestResponse>> updatePendingRequest(
            @PathVariable Long id,
            @Valid @RequestBody CreatePurchaseRequestRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Purchase request updated successfully.",
                purchaseRequestService.updatePendingRequest(id, request)));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<PurchaseRequestResponse>>> getMyRequests(
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PurchaseRequestResponse> response = purchaseRequestService.getMyRequests(pageable);
        return ResponseEntity.ok(ApiResponse.success("My purchase requests fetched successfully.", response));
    }

    @GetMapping("/dashboard-stats")
    public ResponseEntity<ApiResponse<EmployeeDashboardStatsResponse>> getDashboardStats() {
        EmployeeDashboardStatsResponse response = purchaseRequestService.getMyDashboardStats();
        return ResponseEntity.ok(ApiResponse.success("Dashboard stats fetched successfully.", response));
    }

    @GetMapping("/assignment-preview")
    public ResponseEntity<ApiResponse<PurchaseRequestResponse>> getAssignmentPreview() {
        return ResponseEntity.ok(ApiResponse.success(
                "Assignment preview fetched successfully.",
                purchaseRequestService.getAssignmentPreview()));
    }

    @GetMapping("/manager/inbox")
    public ResponseEntity<ApiResponse<Page<PurchaseRequestResponse>>> getManagerInbox(
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(ApiResponse.success(
                "Manager inbox fetched successfully.",
                purchaseRequestService.getManagerInbox(pageable)));
    }

    @GetMapping("/manager/dashboard-stats")
    public ResponseEntity<ApiResponse<ManagerDashboardStatsResponse>> getManagerDashboardStats() {
        return ResponseEntity.ok(ApiResponse.success(
                "Manager dashboard stats fetched successfully.",
                purchaseRequestService.getManagerDashboardStats()));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<PurchaseRequestResponse>> approve(
            @PathVariable Long id,
            @RequestBody(required = false) ManagerDecisionRequest request) {

        return ResponseEntity.ok(ApiResponse.success(
                "Purchase request approved successfully.",
                purchaseRequestService.approve(id, request != null ? request : new ManagerDecisionRequest())));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<PurchaseRequestResponse>> reject(
            @PathVariable Long id,
            @Valid @RequestBody ManagerDecisionRequest request) {

        return ResponseEntity.ok(ApiResponse.success(
                "Purchase request rejected successfully.",
                purchaseRequestService.reject(id, request)));
    }

    @PostMapping("/{id}/return")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<PurchaseRequestResponse>> returnForModification(
            @PathVariable Long id,
            @Valid @RequestBody ManagerDecisionRequest request) {

        return ResponseEntity.ok(ApiResponse.success(
                "Purchase request returned for modification.",
                purchaseRequestService.returnForModification(id, request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PurchaseRequestResponse>> getById(@PathVariable Long id) {
        PurchaseRequestResponse response = purchaseRequestService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Purchase request fetched successfully.", response));
    }
}

package com.procurement.enterprise.controller;

import com.procurement.enterprise.dto.response.ApprovalHierarchyResponse;
import com.procurement.enterprise.dto.response.PurchaseRequestResponse;
import com.procurement.enterprise.exception.ResourceNotFoundException;
import com.procurement.enterprise.service.RequestRoutingService;
import com.procurement.enterprise.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Exposes automatic workflow-routing operations: assigning the first approver
 * of a purchase request from its department's hierarchy, and looking up the
 * next approver level once a request progresses.
 * <p>
 * No UI is required for this module; these endpoints exist so the routing
 * logic can be exercised directly (e.g. by the requisition-creation flow, or
 * for manual testing) without depending on other in-progress modules.
 */
@RestController
@RequestMapping({"/v1/workflow-routing", "/workflow-routing"})
@RequiredArgsConstructor
public class RequestRoutingController {

    private static final Logger log =
            LoggerFactory.getLogger(RequestRoutingController.class);

    private final RequestRoutingService requestRoutingService;

    @PostMapping("/purchase-requests/{purchaseRequestId}/route")
    public ResponseEntity<ApiResponse<PurchaseRequestResponse>> routeNewRequest(
            @PathVariable Long purchaseRequestId) {

        log.info("Routing purchase request {}", purchaseRequestId);

        PurchaseRequestResponse response =
                requestRoutingService.routeNewRequest(purchaseRequestId);

        return ResponseEntity.ok(
                ApiResponse.success("Purchase request routed to first approver successfully", response));
    }

    @GetMapping("/departments/{departmentId}/first-level")
    public ResponseEntity<ApiResponse<ApprovalHierarchyResponse>> getFirstLevel(
            @PathVariable Long departmentId) {

        ApprovalHierarchyResponse response = requestRoutingService
                .getFirstLevel(departmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ApprovalHierarchy", "department", departmentId.toString()));

        return ResponseEntity.ok(
                ApiResponse.success("First hierarchy level fetched successfully", response));
    }

    @GetMapping("/departments/{departmentId}/next-level")
    public ResponseEntity<ApiResponse<ApprovalHierarchyResponse>> getNextLevel(
            @PathVariable Long departmentId,
            @RequestParam Integer currentLevel) {

        ApprovalHierarchyResponse response = requestRoutingService
                .getNextLevel(departmentId, currentLevel)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ApprovalHierarchy",
                                "department",
                                departmentId + " (no level after " + currentLevel + ")"));

        return ResponseEntity.ok(
                ApiResponse.success("Next hierarchy level fetched successfully", response));
    }
}

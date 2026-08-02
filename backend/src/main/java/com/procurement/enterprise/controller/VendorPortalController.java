package com.procurement.enterprise.controller;

import com.procurement.enterprise.dto.request.VendorUpdateDeliveryRequest;
import com.procurement.enterprise.dto.response.*;
import com.procurement.enterprise.enums.PurchaseOrderStatus;
import com.procurement.enterprise.service.VendorPortalService;
import com.procurement.enterprise.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for vendor-facing portal operations.
 * All endpoints require ROLE_VENDOR and operate on the authenticated vendor's data only.
 */
@RestController
@RequestMapping("/v1/vendor-portal")
@RequiredArgsConstructor
@PreAuthorize("hasRole('VENDOR')")
public class VendorPortalController {

    private static final Logger log = LoggerFactory.getLogger(VendorPortalController.class);

    private final VendorPortalService vendorPortalService;

    /* ── Dashboard ───────────────────────────────────────────────── */

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<VendorDashboardResponse>> getDashboard(
            @AuthenticationPrincipal UserDetails principal) {
        log.info("Vendor dashboard requested by: {}", principal.getUsername());
        VendorDashboardResponse data = vendorPortalService.getDashboard(principal.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Dashboard loaded", data));
    }

    /* ── Purchase Orders ─────────────────────────────────────────── */

    @GetMapping("/purchase-orders")
    public ResponseEntity<ApiResponse<Page<VendorPurchaseOrderResponse>>> getPurchaseOrders(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(required = false) PurchaseOrderStatus status,
            @ParameterObject Pageable pageable) {
        Page<VendorPurchaseOrderResponse> page =
                vendorPortalService.getPurchaseOrders(principal.getUsername(), status, pageable);
        return ResponseEntity.ok(ApiResponse.success("Purchase orders fetched", page));
    }

    @GetMapping("/purchase-orders/{id}")
    public ResponseEntity<ApiResponse<VendorPurchaseOrderResponse>> getPurchaseOrderDetail(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id) {
        VendorPurchaseOrderResponse data =
                vendorPortalService.getPurchaseOrderDetail(principal.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.success("Purchase order fetched", data));
    }

    @PostMapping("/purchase-orders/{id}/accept")
    public ResponseEntity<ApiResponse<VendorPurchaseOrderResponse>> acceptOrder(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id) {
        VendorPurchaseOrderResponse data = vendorPortalService.acceptOrder(principal.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.success("Order accepted successfully", data));
    }

    @PostMapping("/purchase-orders/{id}/reject")
    public ResponseEntity<ApiResponse<VendorPurchaseOrderResponse>> rejectOrder(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String remarks = body.getOrDefault("remarks", "");
        VendorPurchaseOrderResponse data = vendorPortalService.rejectOrder(principal.getUsername(), id, remarks);
        return ResponseEntity.ok(ApiResponse.success("Order rejected", data));
    }

    /* ── Delivery ────────────────────────────────────────────────── */

    @PostMapping("/deliveries")
    public ResponseEntity<ApiResponse<DeliveryResponse>> updateDelivery(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody VendorUpdateDeliveryRequest request) {
        DeliveryResponse data = vendorPortalService.updateDelivery(principal.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Delivery updated successfully", data));
    }

    /* ── Profile ─────────────────────────────────────────────────── */

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<VendorResponse>> getProfile(
            @AuthenticationPrincipal UserDetails principal) {
        VendorResponse data = vendorPortalService.getProfile(principal.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Profile fetched", data));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<VendorResponse>> updateProfile(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody Map<String, String> body) {
        VendorResponse data = vendorPortalService.updateProfile(
                principal.getUsername(),
                body.get("contactName"),
                body.get("phone"),
                body.get("address")
        );
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", data));
    }
}

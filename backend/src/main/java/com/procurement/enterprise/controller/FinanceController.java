package com.procurement.enterprise.controller;

import com.procurement.enterprise.dto.request.ApprovePaymentRequest;
import com.procurement.enterprise.dto.response.*;
import com.procurement.enterprise.service.FinanceService;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/finance")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('FINANCE', 'ADMIN')")
public class FinanceController {

    private static final Logger log = LoggerFactory.getLogger(FinanceController.class);

    private final FinanceService financeService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<FinanceDashboardResponse>> getDashboard() {
        log.info("Finance dashboard stats requested");
        return ResponseEntity.ok(ApiResponse.success("Dashboard stats fetched", financeService.getDashboardStats()));
    }

    @GetMapping("/pending-payments")
    public ResponseEntity<ApiResponse<Page<PendingPaymentResponse>>> getPendingPayments(
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Pending payments fetched",
                financeService.getPendingPayments(pageable)));
    }

    @GetMapping("/payment-history")
    public ResponseEntity<ApiResponse<Page<PaymentHistoryResponse>>> getPaymentHistory(
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Payment history fetched",
                financeService.getPaymentHistory(pageable)));
    }

    @GetMapping("/payments/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Payment fetched", financeService.getPaymentById(id)));
    }

    @PostMapping("/payments/{purchaseOrderId}/approve")
    public ResponseEntity<ApiResponse<PaymentResponse>> approvePayment(
            @PathVariable Long purchaseOrderId,
            @Valid @RequestBody ApprovePaymentRequest request) {
        log.info("Approving payment for PO id: {}", purchaseOrderId);
        PaymentResponse response = financeService.approvePayment(purchaseOrderId, request);
        return ResponseEntity.ok(ApiResponse.success("Payment approved successfully", response));
    }

    @PostMapping("/payments/{purchaseOrderId}/cancel")
    public ResponseEntity<ApiResponse<PaymentResponse>> cancelPayment(@PathVariable Long purchaseOrderId) {
        log.info("Cancelling payment for PO id: {}", purchaseOrderId);
        PaymentResponse response = financeService.cancelPayment(purchaseOrderId);
        return ResponseEntity.ok(ApiResponse.success("Payment cancelled", response));
    }
}

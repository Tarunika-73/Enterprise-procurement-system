package com.procurement.enterprise.controller;

import com.procurement.enterprise.dto.purchaserequisition.PurchaseRequisitionRequest;
import com.procurement.enterprise.dto.purchaserequisition.PurchaseRequisitionResponse;
import com.procurement.enterprise.dto.purchaserequisition.PurchaseRequisitionUpdateRequest;
import com.procurement.enterprise.service.PurchaseRequisitionService;
import com.procurement.enterprise.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({
        "/v1/purchase-requisitions",
        "/purchase-requisitions"
})
@RequiredArgsConstructor
public class PurchaseRequisitionController {

    private final PurchaseRequisitionService purchaseRequisitionService;

    /**
     * Create Purchase Requisition
     */
    @PostMapping
    public ApiResponse<PurchaseRequisitionResponse> createPurchaseRequisition(
            @Valid @RequestBody PurchaseRequisitionRequest request) {

        PurchaseRequisitionResponse response =
                purchaseRequisitionService.createPurchaseRequisition(request);

        return ApiResponse.success(
                "Purchase Requisition created successfully.",
                response,
                HttpStatus.CREATED);
    }

    /**
     * View All Purchase Requisitions
     */
    @GetMapping
    public ApiResponse<Page<PurchaseRequisitionResponse>> getAllPurchaseRequisitions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<PurchaseRequisitionResponse> response =
                purchaseRequisitionService.getAllPurchaseRequisitions(pageable);

        return ApiResponse.success(
                "Purchase Requisitions fetched successfully.",
                response);
    }

    /**
     * View Purchase Requisition By Id
     */
    @GetMapping("/{id}")
    public ApiResponse<PurchaseRequisitionResponse> getPurchaseRequisitionById(
            @PathVariable Long id) {

        PurchaseRequisitionResponse response =
                purchaseRequisitionService.getPurchaseRequisitionById(id);

        return ApiResponse.success(
                "Purchase Requisition fetched successfully.",
                response);
    }

    /**
     * Update Purchase Requisition
     */
    @PutMapping("/{id}")
    public ApiResponse<PurchaseRequisitionResponse> updatePurchaseRequisition(
            @PathVariable Long id,
            @Valid @RequestBody PurchaseRequisitionUpdateRequest request) {

        PurchaseRequisitionResponse response =
                purchaseRequisitionService.updatePurchaseRequisition(id, request);

        return ApiResponse.success(
                "Purchase Requisition updated successfully.",
                response);
    }

    /**
     * Soft Delete Purchase Requisition
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePurchaseRequisition(
            @PathVariable Long id) {

        purchaseRequisitionService.deletePurchaseRequisition(id);

        return ApiResponse.success(
                "Purchase Requisition deleted successfully.",
                null);
    }

    /**
     * Search Purchase Requisitions
     */
    @GetMapping("/search")
    public ApiResponse<Page<PurchaseRequisitionResponse>> searchPurchaseRequisitions(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<PurchaseRequisitionResponse> response =
                purchaseRequisitionService.searchPurchaseRequisitions(
                        keyword,
                        pageable);

        return ApiResponse.success(
                "Search completed successfully.",
                response);
    }
}
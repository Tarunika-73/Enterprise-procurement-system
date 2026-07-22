package com.procurement.enterprise.controller;

import com.procurement.enterprise.dto.request.CreateSupplierPerformanceRequest;
import com.procurement.enterprise.dto.response.SupplierPerformanceResponse;
import com.procurement.enterprise.service.SupplierPerformanceService;
import com.procurement.enterprise.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/supplier-performance")
@RequiredArgsConstructor
public class SupplierPerformanceController {

    private final SupplierPerformanceService supplierPerformanceService;

    @PostMapping
    public ResponseEntity<ApiResponse<SupplierPerformanceResponse>> createSupplierPerformance(
            @Valid @RequestBody CreateSupplierPerformanceRequest request) {

        SupplierPerformanceResponse response =
                supplierPerformanceService.createSupplierPerformance(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Supplier performance created successfully",
                        response,
                        HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierPerformanceResponse>> updateSupplierPerformance(
            @PathVariable Long id,
            @Valid @RequestBody CreateSupplierPerformanceRequest request) {

        SupplierPerformanceResponse response =
                supplierPerformanceService.updateSupplierPerformance(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Supplier performance updated successfully",
                        response,
                        HttpStatus.OK));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSupplierPerformance(
            @PathVariable Long id) {

        supplierPerformanceService.deleteSupplierPerformance(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Supplier performance deleted successfully",
                        null,
                        HttpStatus.OK));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierPerformanceResponse>> getSupplierPerformanceById(
            @PathVariable Long id) {

        SupplierPerformanceResponse response =
                supplierPerformanceService.getSupplierPerformanceById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Supplier performance fetched successfully",
                        response,
                        HttpStatus.OK));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<SupplierPerformanceResponse>>> getAllSupplierPerformance(
            Pageable pageable) {

        Page<SupplierPerformanceResponse> response =
                supplierPerformanceService.getAllSupplierPerformance(pageable);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Supplier performance fetched successfully",
                        response,
                        HttpStatus.OK));
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<ApiResponse<Page<SupplierPerformanceResponse>>> getSupplierPerformanceByVendor(
            @PathVariable Long vendorId,
            Pageable pageable) {

        Page<SupplierPerformanceResponse> response =
                supplierPerformanceService.getSupplierPerformanceByVendor(vendorId, pageable);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Supplier performance fetched successfully",
                        response,
                        HttpStatus.OK));
    }

    @GetMapping("/purchase-order/{purchaseOrderId}")
    public ResponseEntity<ApiResponse<Page<SupplierPerformanceResponse>>> getSupplierPerformanceByPurchaseOrder(
            @PathVariable Long purchaseOrderId,
            Pageable pageable) {

        Page<SupplierPerformanceResponse> response =
                supplierPerformanceService.getSupplierPerformanceByPurchaseOrder(
                        purchaseOrderId, pageable);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Supplier performance fetched successfully",
                        response,
                        HttpStatus.OK));
    }

    @GetMapping("/vendor/{vendorId}/average-rating")
    public ResponseEntity<ApiResponse<Double>> getAverageRating(
            @PathVariable Long vendorId) {

        Double averageRating =
                supplierPerformanceService.getAverageRating(vendorId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Average supplier rating fetched successfully",
                        averageRating,
                        HttpStatus.OK));
    }
}
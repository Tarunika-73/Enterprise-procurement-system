package com.procurement.enterprise.controller;

import com.procurement.enterprise.dto.request.CreateSupplierComplianceRequest;
import com.procurement.enterprise.dto.request.UpdateSupplierComplianceRequest;
import com.procurement.enterprise.dto.response.SupplierComplianceResponse;
import com.procurement.enterprise.service.SupplierComplianceService;
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
@RequestMapping({"/v1/supplier-compliance", "/supplier-compliance"})
@RequiredArgsConstructor
public class SupplierComplianceController {

    private static final Logger log =
            LoggerFactory.getLogger(SupplierComplianceController.class);

    private final SupplierComplianceService supplierComplianceService;

    @PostMapping
    public ResponseEntity<ApiResponse<SupplierComplianceResponse>> createCompliance(
            @Valid @RequestBody CreateSupplierComplianceRequest request) {

        log.info("Creating supplier compliance for vendor: {}", request.getVendorId());

        SupplierComplianceResponse response =
                supplierComplianceService.createCompliance(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Supplier compliance created successfully",
                        response,
                        HttpStatus.CREATED));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<SupplierComplianceResponse>>> getAllCompliance(
            @ParameterObject Pageable pageable) {

        Page<SupplierComplianceResponse> response =
                supplierComplianceService.getAllCompliance(pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Supplier compliance fetched successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierComplianceResponse>> getComplianceById(
            @PathVariable Long id) {

        SupplierComplianceResponse response =
                supplierComplianceService.getComplianceById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Supplier compliance fetched successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierComplianceResponse>> updateCompliance(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSupplierComplianceRequest request) {

        SupplierComplianceResponse response =
                supplierComplianceService.updateCompliance(id, request);

        return ResponseEntity.ok(
                ApiResponse.success("Supplier compliance updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCompliance(
            @PathVariable Long id) {

        supplierComplianceService.deleteCompliance(id);

        return ResponseEntity.ok(
                ApiResponse.success("Supplier compliance deleted successfully", null));
    }
}
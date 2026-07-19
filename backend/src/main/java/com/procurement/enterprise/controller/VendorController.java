package com.procurement.enterprise.controller;

import com.procurement.enterprise.dto.request.CreateVendorRequest;
import com.procurement.enterprise.dto.request.UpdateVendorRequest;
import com.procurement.enterprise.dto.response.VendorResponse;
import com.procurement.enterprise.service.VendorService;
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

/**
 * REST controller for vendor registration and profile management.
 */
@RestController
@RequestMapping({"/v1/vendors", "/vendors"})
@RequiredArgsConstructor
public class VendorController {

    private static final Logger log = LoggerFactory.getLogger(VendorController.class);

    private final VendorService vendorService;

    @PostMapping
    public ResponseEntity<ApiResponse<VendorResponse>> createVendor(
            @Valid @RequestBody CreateVendorRequest request) {

        log.info("Creating vendor with email: {}", request.getEmail());

        VendorResponse response = vendorService.createVendor(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Vendor created successfully",
                        response,
                        HttpStatus.CREATED));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<VendorResponse>>> getAllVendors(
            @ParameterObject Pageable pageable) {

        log.info("Fetching all vendors");

        Page<VendorResponse> vendors = vendorService.getAllVendors(pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Vendors fetched successfully", vendors));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VendorResponse>> getVendorById(
            @PathVariable Long id) {

        log.info("Fetching vendor with id: {}", id);

        VendorResponse response = vendorService.getVendorById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Vendor fetched successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VendorResponse>> updateVendor(
            @PathVariable Long id,
            @Valid @RequestBody UpdateVendorRequest request) {

        log.info("Updating vendor with id: {}", id);

        VendorResponse response = vendorService.updateVendor(id, request);

        return ResponseEntity.ok(
                ApiResponse.success("Vendor updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVendor(
            @PathVariable Long id) {

        log.info("Deleting vendor with id: {}", id);

        vendorService.deleteVendor(id);

        return ResponseEntity.ok(
                ApiResponse.success("Vendor deleted successfully", null));
    }
}
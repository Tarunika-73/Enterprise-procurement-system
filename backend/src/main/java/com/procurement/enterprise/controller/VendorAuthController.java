package com.procurement.enterprise.controller;

import com.procurement.enterprise.dto.request.VendorLoginRequest;
import com.procurement.enterprise.dto.request.VendorRegisterRequest;
import com.procurement.enterprise.dto.response.VendorRegisterResponse;
import com.procurement.enterprise.service.VendorAuthenticationService;
import com.procurement.enterprise.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Vendor-specific authentication controller.
 * Completely separate from {@link AuthController} — does NOT touch the users table.
 */
@RestController
@RequestMapping("/vendor/auth")
@RequiredArgsConstructor
public class VendorAuthController {

    private final VendorAuthenticationService vendorAuthenticationService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(
            @Valid @RequestBody VendorLoginRequest request) {
        Map<String, Object> payload = vendorAuthenticationService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Vendor login successful", payload));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<VendorRegisterResponse>> register(
            @Valid @RequestBody VendorRegisterRequest request) {
        VendorRegisterResponse response = vendorAuthenticationService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Vendor registered successfully", response, HttpStatus.CREATED));
    }
}

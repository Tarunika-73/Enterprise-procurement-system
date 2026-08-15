package com.procurement.enterprise.controller;

import com.procurement.enterprise.dto.response.VendorRecommendationResponse;
import com.procurement.enterprise.service.VendorRecommendationService;
import com.procurement.enterprise.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/procurement/purchase-requests")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('PROCUREMENT_OFFICER', 'ADMIN')")
public class VendorRecommendationController {

    private final VendorRecommendationService vendorRecommendationService;

    /**
     * POST /v1/procurement/purchase-requests/{purchaseRequestId}/recommend-vendor
     *
     * Returns a ranked list of eligible vendors for the given approved purchase request.
     * The recommendation is advisory only — it does NOT assign a vendor or create a PO.
     * Only PROCUREMENT_OFFICER and ADMIN may call this endpoint.
     */
    @PostMapping("/{purchaseRequestId}/recommend-vendor")
    public ResponseEntity<ApiResponse<VendorRecommendationResponse>> recommend(
            @PathVariable Long purchaseRequestId) {
        VendorRecommendationResponse result = vendorRecommendationService
                .recommendForPurchaseRequest(purchaseRequestId);
        String message = result.recommendedVendor() != null
                ? "Vendor recommendation generated successfully."
                : result.message();
        return ResponseEntity.ok(ApiResponse.success(message, result));
    }
}

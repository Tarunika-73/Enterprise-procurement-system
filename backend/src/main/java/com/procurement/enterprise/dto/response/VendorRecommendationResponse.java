package com.procurement.enterprise.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record VendorRecommendationResponse(
        Long purchaseRequestId,
        String product,
        Integer requestedQuantity,
        VendorRanking recommendedVendor,
        List<VendorRanking> rankings,
        List<IneligibleVendor> ineligibleVendors,
        String message
) {
    public record VendorRanking(
            Long vendorId,
            String vendorName,
            BigDecimal unitPrice,
            Integer availableQuantity,
            double overallScore,
            double priceScore,
            double availabilityScore,
            double qualityScore,
            double deliveryScore,
            double historicalScore,
            Integer leadTimeDays,
            long historicalOrders,
            long successfulOrders
    ) {}

    public record IneligibleVendor(
            Long vendorId,
            String vendorName,
            BigDecimal unitPrice,
            Integer availableQuantity,
            String reason
    ) {}
}

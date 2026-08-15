package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.response.VendorRecommendationResponse;

public interface VendorRecommendationService {
    VendorRecommendationResponse recommendForPurchaseRequest(Long purchaseRequestId);
}

package com.procurement.enterprise.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class VendorDashboardResponse {
    private Long vendorId;
    private String vendorName;
    private long totalOrders;
    private long pendingDelivery;
    private long deliveredOrders;
    private BigDecimal totalOrderValue;
    private List<VendorPurchaseOrderResponse> recentOrders;
}

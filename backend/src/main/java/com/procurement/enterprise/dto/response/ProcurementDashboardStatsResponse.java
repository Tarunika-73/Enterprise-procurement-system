package com.procurement.enterprise.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProcurementDashboardStatsResponse {
    private long approvedRequests;
    private long totalPurchaseOrders;
    private long activeVendors;
    private long pendingDeliveries;
}

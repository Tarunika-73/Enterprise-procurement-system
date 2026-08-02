package com.procurement.enterprise.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ManagerDashboardStatsResponse {
    private long pendingRequests;
    private long approvedRequests;
    private long rejectedRequests;
    private long returnedRequests;
    private long totalAssignedRequests;
    private List<PurchaseRequestResponse> recentRequests;
}

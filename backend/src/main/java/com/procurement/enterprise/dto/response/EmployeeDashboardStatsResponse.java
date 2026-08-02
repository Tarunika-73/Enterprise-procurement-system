package com.procurement.enterprise.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class EmployeeDashboardStatsResponse {
    private long totalRequests;
    private long pendingRequests;
    private long approvedRequests;
    private long rejectedRequests;
    private long unreadNotifications;
    private List<PurchaseRequestResponse> recentRequests;
}

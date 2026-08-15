package com.procurement.enterprise.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminDashboardStatsResponse {

    // Users
    private long totalUsers;
    private long activeUsers;
    private long inactiveUsers;

    // Vendors
    private long totalVendors;
    private long activeVendors;

    // Purchase Requests
    private long totalPurchaseRequests;
    private long pendingPurchaseRequests;
    private long approvedPurchaseRequests;
    private long rejectedPurchaseRequests;

    // Purchase Orders
    private long totalPurchaseOrders;
    private long pendingPurchaseOrders;

    // Finance
    private long totalInvoices;
    private long pendingPayments;
    private long completedPayments;
}

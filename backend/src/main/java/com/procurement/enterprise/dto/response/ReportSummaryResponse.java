package com.procurement.enterprise.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * Role-scoped summary for the Reports page.
 * <p>
 * The same endpoint is used by every internal role; {@code scope} tells the
 * frontend whether the numbers below cover just the current user, their
 * department, or the whole organization, so it can label things correctly.
 */
@Getter
@Builder
public class ReportSummaryResponse {

    /** "SELF", "DEPARTMENT", or "ORGANIZATION" */
    private String scope;
    private String scopeName;

    private long totalRequests;
    private long approvedRequests;
    private long purchaseOrders;
    private long activeVendors;

    private List<DepartmentSpendResponse> departmentBreakdown;
    private Map<String, Long> requestStatusBreakdown;
    private List<MonthlySpendResponse> monthlySpend;
    private List<RecentActivityResponse> recentActivity;
}

package com.procurement.enterprise.dto.response;

import java.math.BigDecimal;
import java.util.List;

/** Compact, role-scoped datasets used by the dashboard charts. */
public record DashboardAnalyticsResponse(
        List<StatusDistribution> requestStatus,
        List<MonthlyTrendPoint> monthlyTrend,
        List<DepartmentSpending> departmentSpending
) {
    public record StatusDistribution(String status, long count) { }
    public record MonthlyTrendPoint(String month, long count) { }
    public record DepartmentSpending(String department, BigDecimal amount) { }
}

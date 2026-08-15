package com.procurement.enterprise.controller;

import com.procurement.enterprise.dto.response.DashboardAnalyticsResponse;
import com.procurement.enterprise.service.DashboardAnalyticsService;
import com.procurement.enterprise.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'PROCUREMENT_OFFICER', 'VENDOR', 'FINANCE', 'ADMIN')")
public class DashboardAnalyticsController {
    private final DashboardAnalyticsService dashboardAnalyticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardAnalyticsResponse>> getDashboardAnalytics() {
        return ResponseEntity.ok(ApiResponse.success("Dashboard analytics fetched", dashboardAnalyticsService.getAnalytics()));
    }
}

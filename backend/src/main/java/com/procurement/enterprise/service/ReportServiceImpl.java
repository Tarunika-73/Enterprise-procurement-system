package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.response.DepartmentSpendResponse;
import com.procurement.enterprise.dto.response.RecentActivityResponse;
import com.procurement.enterprise.dto.response.ReportSummaryResponse;
import com.procurement.enterprise.entity.Department;
import com.procurement.enterprise.entity.PurchaseOrder;
import com.procurement.enterprise.entity.PurchaseRequest;
import com.procurement.enterprise.entity.User;
import com.procurement.enterprise.enums.PurchaseRequestStatus;
import com.procurement.enterprise.exception.UnauthorizedException;
import com.procurement.enterprise.repository.DepartmentRepository;
import com.procurement.enterprise.repository.PurchaseOrderRepository;
import com.procurement.enterprise.repository.PurchaseRequestRepository;
import com.procurement.enterprise.repository.UserRepository;
import com.procurement.enterprise.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final UserRepository userRepository;
    private final PurchaseRequestRepository purchaseRequestRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final VendorRepository vendorRepository;
    private final DepartmentRepository departmentRepository;

    private static final int ACTIVITY_LIMIT = 6;

    @Override
    @Transactional(readOnly = true)
    public ReportSummaryResponse getSummary() {
        User user = getCurrentUser();
        String role = normalizeRole(user.getRole() != null ? user.getRole().getName() : null);

        if ("EMPLOYEE".equals(role)) {
            return buildSelfScopedSummary(user);
        }
        if ("MANAGER".equals(role)) {
            return buildDepartmentScopedSummary(user);
        }
        // Procurement Officer, Finance, Admin (and any unrecognized internal role) see the full org.
        return buildOrganizationScopedSummary();
    }

    @Override
    @Transactional(readOnly = true)
    public String generateReportCsv() {
        return buildCsv(getSummary());
    }

    /* ── scope builders ──────────────────────────────────────────── */

    private ReportSummaryResponse buildSelfScopedSummary(User user) {
        Long userId = user.getId();

        long totalRequests = purchaseRequestRepository.countByRequesterIdAndIsDeletedFalse(userId);
        long approvedRequests = countApproved(
                purchaseRequestRepository.countByRequesterIdAndStatusAndIsDeletedFalse(userId, PurchaseRequestStatus.APPROVED),
                purchaseRequestRepository.countByRequesterIdAndStatusAndIsDeletedFalse(userId, PurchaseRequestStatus.CLOSED));
        long purchaseOrders = purchaseOrderRepository.countByPurchaseRequest_Requester_IdAndIsDeletedFalse(userId);
        long activeVendors = vendorRepository.countByIsActiveTrueAndIsDeletedFalse();

        List<PurchaseRequest> ownRequests = purchaseRequestRepository
                .findByRequesterIdAndIsDeletedFalse(userId, Pageable.unpaged())
                .getContent();
        BigDecimal ownSpend = sumAmounts(ownRequests);

        DepartmentSpendResponse breakdown = DepartmentSpendResponse.builder()
                .departmentId(user.getDepartment() != null ? user.getDepartment().getId() : null)
                .departmentName(user.getDepartment() != null ? user.getDepartment().getName() : "Unassigned")
                .requestCount(totalRequests)
                .totalSpend(ownSpend)
                .relativePercent(100)
                .build();

        List<PurchaseRequest> recentPRs = purchaseRequestRepository
                .findByRequesterIdAndIsDeletedFalseOrderByCreatedAtDesc(userId, PageRequest.of(0, ACTIVITY_LIMIT))
                .getContent();
        List<PurchaseOrder> recentPOs = purchaseOrderRepository
                .findTop5ByPurchaseRequest_Requester_IdAndIsDeletedFalseOrderByCreatedAtDesc(userId);

        return ReportSummaryResponse.builder()
                .scope("SELF")
                .scopeName(user.getFirstName() + " " + user.getLastName())
                .totalRequests(totalRequests)
                .approvedRequests(approvedRequests)
                .purchaseOrders(purchaseOrders)
                .activeVendors(activeVendors)
                .departmentBreakdown(List.of(breakdown))
                .requestStatusBreakdown(statusBreakdown(ownRequests))
                .monthlySpend(monthlySpend(ownRequests))
                .recentActivity(mergeActivity(recentPRs, recentPOs))
                .build();
    }

    private ReportSummaryResponse buildDepartmentScopedSummary(User user) {
        if (user.getDepartment() == null) {
            // Manager with no assigned department: fall back to an empty, org-shaped response.
            return emptySummary("DEPARTMENT", "Unassigned");
        }

        Long deptId = user.getDepartment().getId();
        String deptName = user.getDepartment().getName();

        long totalRequests = purchaseRequestRepository.countByDepartmentIdAndIsDeletedFalse(deptId);
        long approvedRequests = countApproved(
                purchaseRequestRepository.countByDepartmentIdAndStatusAndIsDeletedFalse(deptId, PurchaseRequestStatus.APPROVED),
                purchaseRequestRepository.countByDepartmentIdAndStatusAndIsDeletedFalse(deptId, PurchaseRequestStatus.CLOSED));
        long purchaseOrders = purchaseOrderRepository.countByPurchaseRequest_Department_IdAndIsDeletedFalse(deptId);
        long activeVendors = vendorRepository.countByIsActiveTrueAndIsDeletedFalse();

        List<PurchaseRequest> deptRequests = purchaseRequestRepository
                .findByDepartmentIdAndIsDeletedFalse(deptId, Pageable.unpaged())
                .getContent();
        BigDecimal deptSpend = sumAmounts(deptRequests);

        DepartmentSpendResponse breakdown = DepartmentSpendResponse.builder()
                .departmentId(deptId)
                .departmentName(deptName)
                .requestCount(totalRequests)
                .totalSpend(deptSpend)
                .relativePercent(100)
                .build();

        List<PurchaseRequest> recentPRs = purchaseRequestRepository
                .findByDepartmentIdAndIsDeletedFalseOrderByCreatedAtDesc(deptId, PageRequest.of(0, ACTIVITY_LIMIT))
                .getContent();
        List<PurchaseOrder> recentPOs = purchaseOrderRepository
                .findTop5ByPurchaseRequest_Department_IdAndIsDeletedFalseOrderByCreatedAtDesc(deptId);

        return ReportSummaryResponse.builder()
                .scope("DEPARTMENT")
                .scopeName(deptName)
                .totalRequests(totalRequests)
                .approvedRequests(approvedRequests)
                .purchaseOrders(purchaseOrders)
                .activeVendors(activeVendors)
                .departmentBreakdown(List.of(breakdown))
                .requestStatusBreakdown(statusBreakdown(deptRequests))
                .monthlySpend(monthlySpend(deptRequests))
                .recentActivity(mergeActivity(recentPRs, recentPOs))
                .build();
    }

    private ReportSummaryResponse buildOrganizationScopedSummary() {
        long totalRequests = purchaseRequestRepository.countByIsDeletedFalse();
        long approvedRequests = countApproved(
                purchaseRequestRepository.countByStatusAndIsDeletedFalse(PurchaseRequestStatus.APPROVED),
                purchaseRequestRepository.countByStatusAndIsDeletedFalse(PurchaseRequestStatus.CLOSED));
        long purchaseOrders = purchaseOrderRepository.countByIsDeletedFalse();
        long activeVendors = vendorRepository.countByIsActiveTrueAndIsDeletedFalse();

        List<Department> departments = departmentRepository.findAllByIsDeletedFalse();
        List<DepartmentSpendResponse> rawBreakdown = new ArrayList<>();
        for (Department dept : departments) {
            long requestCount = purchaseRequestRepository.countByDepartmentIdAndIsDeletedFalse(dept.getId());
            List<PurchaseRequest> deptRequests = purchaseRequestRepository
                    .findByDepartmentIdAndIsDeletedFalse(dept.getId(), Pageable.unpaged())
                    .getContent();
            BigDecimal spend = sumAmounts(deptRequests);
            rawBreakdown.add(DepartmentSpendResponse.builder()
                    .departmentId(dept.getId())
                    .departmentName(dept.getName())
                    .requestCount(requestCount)
                    .totalSpend(spend)
                    .build());
        }

        BigDecimal maxSpend = rawBreakdown.stream()
                .map(DepartmentSpendResponse::getTotalSpend)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        List<DepartmentSpendResponse> departmentBreakdown = rawBreakdown.stream()
                .map(item -> DepartmentSpendResponse.builder()
                        .departmentId(item.getDepartmentId())
                        .departmentName(item.getDepartmentName())
                        .requestCount(item.getRequestCount())
                        .totalSpend(item.getTotalSpend())
                        .relativePercent(relativePercent(item.getTotalSpend(), maxSpend))
                        .build())
                .sorted(Comparator.comparing(DepartmentSpendResponse::getTotalSpend).reversed())
                .toList();

        List<PurchaseRequest> recentPRs = purchaseRequestRepository
                .findAllByIsDeletedFalseOrderByCreatedAtDesc(PageRequest.of(0, ACTIVITY_LIMIT))
                .getContent();
        List<PurchaseRequest> organizationRequests = purchaseRequestRepository
                .findAllByIsDeletedFalse(Pageable.unpaged()).getContent();
        List<PurchaseOrder> recentPOs = purchaseOrderRepository.findTop5ByIsDeletedFalseOrderByCreatedAtDesc();

        return ReportSummaryResponse.builder()
                .scope("ORGANIZATION")
                .scopeName("Organization")
                .totalRequests(totalRequests)
                .approvedRequests(approvedRequests)
                .purchaseOrders(purchaseOrders)
                .activeVendors(activeVendors)
                .departmentBreakdown(departmentBreakdown)
                .requestStatusBreakdown(statusBreakdown(organizationRequests))
                .monthlySpend(monthlySpend(organizationRequests))
                .recentActivity(mergeActivity(recentPRs, recentPOs))
                .build();
    }

    private ReportSummaryResponse emptySummary(String scope, String scopeName) {
        return ReportSummaryResponse.builder()
                .scope(scope)
                .scopeName(scopeName)
                .totalRequests(0)
                .approvedRequests(0)
                .purchaseOrders(0)
                .activeVendors(vendorRepository.countByIsActiveTrueAndIsDeletedFalse())
                .departmentBreakdown(List.of())
                .requestStatusBreakdown(Map.of())
                .monthlySpend(List.of())
                .recentActivity(List.of())
                .build();
    }

    /* ── CSV export ──────────────────────────────────────────────── */

    private String buildCsv(ReportSummaryResponse summary) {
        java.time.format.DateTimeFormatter tsFormat =
                java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm");
        StringBuilder html = new StringBuilder("""
                <html><head><meta charset=\"UTF-8\"><style>
                body{font-family:Arial,sans-serif;color:#1f2937;padding:22px}table{border-collapse:collapse;width:100%;margin:12px 0 24px}th{background:#5b4fc4;color:#fff;text-align:left;padding:9px;border:1px solid #5b4fc4}td{padding:8px;border:1px solid #d9dce3}.title{font-size:22px;font-weight:bold;color:#5b4fc4}.subtitle{font-size:15px;font-weight:bold;color:#374151}.meta{color:#6b7280}.section{background:#eef0ff;color:#3f3690;font-weight:bold;padding:8px}.number{text-align:right}.footer{margin-top:28px;border-top:1px solid #d9dce3;padding-top:10px;color:#6b7280;font-size:11px}
                </style></head><body>
                <div class=\"title\">ENTERPRISE PROCUREMENT SYSTEM</div><div class=\"subtitle\">Procurement / Financial Report</div>
                <p class=\"meta\">Report scope: <b>""")
                .append(htmlValue(summary.getScopeName())).append("</b><br>Generated: ")
                .append(LocalDateTime.now().format(tsFormat)).append("</p>");
        html.append("<div class=\"section\">SUMMARY</div><table><tr><th>Metric</th><th class=\"number\">Value</th></tr>");
        html.append(metricRow("Total Requests", String.valueOf(summary.getTotalRequests())));
        html.append(metricRow("Approved Requests", String.valueOf(summary.getApprovedRequests())));
        html.append(metricRow("Purchase Orders", String.valueOf(summary.getPurchaseOrders())));
        html.append(metricRow("Active Vendors", String.valueOf(summary.getActiveVendors())));
        html.append("</table><div class=\"section\">DEPARTMENT SPEND BREAKDOWN</div><table><tr><th>Department</th><th class=\"number\">Request Count</th><th class=\"number\">Total Spend (INR)</th><th class=\"number\">Relative %</th></tr>");
        List<DepartmentSpendResponse> breakdown = summary.getDepartmentBreakdown();
        if (breakdown == null || breakdown.isEmpty()) {
            html.append("<tr><td colspan=\"4\">No data available</td></tr>");
        } else {
            for (DepartmentSpendResponse dept : breakdown) {
                html.append("<tr><td>").append(htmlValue(dept.getDepartmentName())).append("</td><td class=\"number\">")
                        .append(dept.getRequestCount()).append("</td><td class=\"number\">₹")
                        .append((dept.getTotalSpend() != null ? dept.getTotalSpend() : BigDecimal.ZERO).setScale(2)).append("</td><td class=\"number\">")
                        .append(dept.getRelativePercent()).append("%</td></tr>");
            }
        }
        html.append("</table><div class=\"section\">RECENT ACTIVITY</div><table><tr><th>Description</th><th>Timestamp</th></tr>");
        List<RecentActivityResponse> activity = summary.getRecentActivity();
        if (activity == null || activity.isEmpty()) {
            html.append("<tr><td colspan=\"2\">No recent activity</td></tr>");
        } else {
            for (RecentActivityResponse item : activity) {
                String ts = item.getTimestamp() != null ? item.getTimestamp().format(tsFormat) : "";
                html.append("<tr><td>").append(htmlValue(item.getDescription())).append("</td><td>").append(htmlValue(ts)).append("</td></tr>");
            }
        }
        return html.append("</table><div class=\"footer\">Enterprise Procurement System &nbsp;|&nbsp; Confidential / Internal Use</div></body></html>").toString();
    }

    private String metricRow(String metric, String value) {
        return "<tr><td>" + htmlValue(metric) + "</td><td class=\"number\">" + htmlValue(value) + "</td></tr>";
    }

    private String htmlValue(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    /** Quotes a CSV field and escapes embedded quotes, only when needed. */
    private String csvValue(String value) {
        if (value == null) return "";
        boolean needsQuoting = value.contains(",") || value.contains("\"") || value.contains("\n");
        String escaped = value.replace("\"", "\"\"");
        return needsQuoting ? "\"" + escaped + "\"" : escaped;
    }

    /* ── helpers ─────────────────────────────────────────────────── */

    private long countApproved(long approved, long closed) {
        return approved + closed;
    }

    private BigDecimal sumAmounts(List<PurchaseRequest> requests) {
        return requests.stream()
                .map(pr -> pr.getTotalAmount() != null ? pr.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private int relativePercent(BigDecimal value, BigDecimal max) {
        if (max == null || max.compareTo(BigDecimal.ZERO) <= 0 || value == null) {
            return 0;
        }
        return value.multiply(BigDecimal.valueOf(100))
                .divide(max, 0, java.math.RoundingMode.HALF_UP)
                .intValue();
    }

    private Map<String, Long> statusBreakdown(List<PurchaseRequest> requests) {
        Map<String, Long> result = new LinkedHashMap<>();
        for (PurchaseRequest request : requests) {
            String status = request.getStatus() != null ? request.getStatus().name() : "UNKNOWN";
            result.merge(status, 1L, Long::sum);
        }
        return result;
    }

    private List<com.procurement.enterprise.dto.response.MonthlySpendResponse> monthlySpend(List<PurchaseRequest> requests) {
        Map<YearMonth, BigDecimal> totals = new LinkedHashMap<>();
        YearMonth current = YearMonth.now();
        for (int offset = 5; offset >= 0; offset--) {
            totals.put(current.minusMonths(offset), BigDecimal.ZERO);
        }
        for (PurchaseRequest request : requests) {
            if (request.getCreatedAt() == null) continue;
            YearMonth month = YearMonth.from(request.getCreatedAt());
            if (totals.containsKey(month)) {
                totals.merge(month, request.getTotalAmount() != null ? request.getTotalAmount() : BigDecimal.ZERO, BigDecimal::add);
            }
        }
        DateTimeFormatter label = DateTimeFormatter.ofPattern("MMM yyyy");
        return totals.entrySet().stream()
                .map(entry -> com.procurement.enterprise.dto.response.MonthlySpendResponse.builder()
                        .month(entry.getKey().format(label)).amount(entry.getValue()).build())
                .toList();
    }

    private List<RecentActivityResponse> mergeActivity(List<PurchaseRequest> prs, List<PurchaseOrder> pos) {
        List<RecentActivityResponse> combined = new ArrayList<>();

        for (PurchaseRequest pr : prs) {
            LocalDateTime timestamp = pr.getUpdatedAt() != null ? pr.getUpdatedAt() : pr.getCreatedAt();
            String description = "Purchase Request " + pr.getRequestNumber() + " " + humanize(pr.getStatus().name());
            combined.add(RecentActivityResponse.builder()
                    .description(description)
                    .timestamp(timestamp)
                    .build());
        }

        for (PurchaseOrder po : pos) {
            LocalDateTime timestamp = po.getUpdatedAt() != null ? po.getUpdatedAt() : po.getCreatedAt();
            String vendorName = po.getVendor() != null ? po.getVendor().getVendorName() : "vendor";
            String description = "Purchase Order " + po.getPurchaseOrderNumber() + " "
                    + humanize(po.getStatus().name()) + " (" + vendorName + ")";
            combined.add(RecentActivityResponse.builder()
                    .description(description)
                    .timestamp(timestamp)
                    .build());
        }

        return combined.stream()
                .sorted(Comparator.comparing(RecentActivityResponse::getTimestamp,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(ACTIVITY_LIMIT)
                .toList();
    }

    private String humanize(String enumName) {
        if (enumName == null || enumName.isBlank()) return "";
        String lower = enumName.toLowerCase(Locale.ROOT).replace('_', ' ');
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    private String normalizeRole(String roleName) {
        if (roleName == null) return null;
        String key = roleName.trim().toLowerCase(Locale.ROOT);
        return switch (key) {
            case "admin" -> "ADMIN";
            case "employee" -> "EMPLOYEE";
            case "manager", "department manager" -> "MANAGER";
            case "vendor" -> "VENDOR";
            case "finance", "finance officer" -> "FINANCE";
            case "procurement officer" -> "PROCUREMENT_OFFICER";
            default -> key.toUpperCase(Locale.ROOT);
        };
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Authentication required.");
        }
        return userRepository.findByEmailAndIsDeletedFalse(authentication.getName())
                .orElseThrow(() -> new UnauthorizedException("Authenticated user not found."));
    }
}

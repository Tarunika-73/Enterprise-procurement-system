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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

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
        List<PurchaseOrder> recentPOs = purchaseOrderRepository.findTop5ByIsDeletedFalseOrderByCreatedAtDesc();

        return ReportSummaryResponse.builder()
                .scope("ORGANIZATION")
                .scopeName("Organization")
                .totalRequests(totalRequests)
                .approvedRequests(approvedRequests)
                .purchaseOrders(purchaseOrders)
                .activeVendors(activeVendors)
                .departmentBreakdown(departmentBreakdown)
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
                .recentActivity(List.of())
                .build();
    }

    /* ── CSV export ──────────────────────────────────────────────── */

    private String buildCsv(ReportSummaryResponse summary) {
        StringBuilder csv = new StringBuilder();
        java.time.format.DateTimeFormatter tsFormat =
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        csv.append("Report Scope,").append(csvValue(summary.getScope())).append('\n');
        csv.append("Scope Name,").append(csvValue(summary.getScopeName())).append('\n');
        csv.append("Generated At,").append(csvValue(LocalDateTime.now().format(tsFormat))).append('\n');
        csv.append('\n');

        csv.append("Summary\n");
        csv.append("Metric,Value\n");
        csv.append("Total Requests,").append(summary.getTotalRequests()).append('\n');
        csv.append("Approved Requests,").append(summary.getApprovedRequests()).append('\n');
        csv.append("Purchase Orders,").append(summary.getPurchaseOrders()).append('\n');
        csv.append("Active Vendors,").append(summary.getActiveVendors()).append('\n');
        csv.append('\n');

        csv.append("Department Breakdown\n");
        csv.append("Department,Request Count,Total Spend,Relative %\n");
        List<DepartmentSpendResponse> breakdown = summary.getDepartmentBreakdown();
        if (breakdown == null || breakdown.isEmpty()) {
            csv.append("No data,,,\n");
        } else {
            for (DepartmentSpendResponse dept : breakdown) {
                csv.append(csvValue(dept.getDepartmentName())).append(',')
                        .append(dept.getRequestCount()).append(',')
                        .append(dept.getTotalSpend() != null ? dept.getTotalSpend() : BigDecimal.ZERO).append(',')
                        .append(dept.getRelativePercent()).append('\n');
            }
        }
        csv.append('\n');

        csv.append("Recent Activity\n");
        csv.append("Description,Timestamp\n");
        List<RecentActivityResponse> activity = summary.getRecentActivity();
        if (activity == null || activity.isEmpty()) {
            csv.append("No recent activity,\n");
        } else {
            for (RecentActivityResponse item : activity) {
                String ts = item.getTimestamp() != null ? item.getTimestamp().format(tsFormat) : "";
                csv.append(csvValue(item.getDescription())).append(',').append(csvValue(ts)).append('\n');
            }
        }

        return csv.toString();
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

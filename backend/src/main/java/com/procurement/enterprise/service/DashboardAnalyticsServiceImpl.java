package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.response.DashboardAnalyticsResponse;
import com.procurement.enterprise.entity.User;
import com.procurement.enterprise.entity.Vendor;
import com.procurement.enterprise.exception.UnauthorizedException;
import com.procurement.enterprise.repository.PurchaseOrderRepository;
import com.procurement.enterprise.repository.PurchaseRequestRepository;
import com.procurement.enterprise.repository.UserRepository;
import com.procurement.enterprise.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardAnalyticsServiceImpl implements DashboardAnalyticsService {
    private static final DateTimeFormatter MONTH_LABEL = DateTimeFormatter.ofPattern("MMM uuuu");
    private final PurchaseRequestRepository purchaseRequestRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardAnalyticsResponse getAnalytics() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) throw new UnauthorizedException("Authentication required.");
        Scope scope = scope(authentication);
        LocalDateTime from = YearMonth.now().minusMonths(5).atDay(1).atStartOfDay();
        return new DashboardAnalyticsResponse(status(scope), trend(scope, from), spending(scope));
    }

    private Scope scope(Authentication auth) {
        String authority = auth.getAuthorities().stream().map(a -> a.getAuthority()).findFirst().orElse("");
        if ("ROLE_VENDOR".equals(authority)) {
            Vendor vendor = vendorRepository.findByEmailAndIsDeletedFalse(auth.getName())
                    .orElseThrow(() -> new UnauthorizedException("Authenticated vendor not found."));
            return new Scope(Type.VENDOR, vendor.getId());
        }
        User user = userRepository.findByEmailAndIsDeletedFalse(auth.getName())
                .orElseThrow(() -> new UnauthorizedException("Authenticated user not found."));
        return switch (authority) {
            case "ROLE_EMPLOYEE" -> new Scope(Type.REQUESTER, user.getId());
            case "ROLE_MANAGER" -> new Scope(Type.MANAGER, user.getId());
            case "ROLE_PROCUREMENT_OFFICER", "ROLE_FINANCE", "ROLE_ADMIN" -> new Scope(Type.ORGANIZATION, null);
            default -> throw new UnauthorizedException("You are not authorized to view analytics.");
        };
    }

    private List<DashboardAnalyticsResponse.StatusDistribution> status(Scope scope) {
        List<Object[]> rows = switch (scope.type) {
            case REQUESTER -> purchaseRequestRepository.countByStatusForRequester(scope.id);
            case MANAGER -> purchaseRequestRepository.countByStatusForManager(scope.id);
            case VENDOR -> purchaseRequestRepository.countByStatusForVendor(scope.id);
            case ORGANIZATION -> purchaseRequestRepository.countByStatusForOrganization();
        };
        return rows.stream().map(row -> new DashboardAnalyticsResponse.StatusDistribution(String.valueOf(row[0]), ((Number) row[1]).longValue())).toList();
    }

    private List<DashboardAnalyticsResponse.MonthlyTrendPoint> trend(Scope scope, LocalDateTime from) {
        List<Object[]> rows = switch (scope.type) {
            case REQUESTER -> purchaseRequestRepository.monthlyTrendForRequester(scope.id, from);
            case MANAGER -> purchaseRequestRepository.monthlyTrendForManager(scope.id, from);
            case VENDOR -> purchaseRequestRepository.monthlyTrendForVendor(scope.id, from);
            case ORGANIZATION -> purchaseRequestRepository.monthlyTrendForOrganization(from);
        };
        return rows.stream().map(row -> new DashboardAnalyticsResponse.MonthlyTrendPoint(
                YearMonth.of(((Number) row[0]).intValue(), ((Number) row[1]).intValue()).format(MONTH_LABEL),
                ((Number) row[2]).longValue())).toList();
    }

    private List<DashboardAnalyticsResponse.DepartmentSpending> spending(Scope scope) {
        List<Object[]> rows = switch (scope.type) {
            case REQUESTER -> purchaseOrderRepository.spendingByDepartmentForRequester(scope.id);
            case MANAGER -> purchaseOrderRepository.spendingByDepartmentForManager(scope.id);
            case VENDOR -> purchaseOrderRepository.spendingByDepartmentForVendor(scope.id);
            case ORGANIZATION -> purchaseOrderRepository.spendingByDepartmentForOrganization();
        };
        return rows.stream().map(row -> new DashboardAnalyticsResponse.DepartmentSpending(
                String.valueOf(row[0]), (BigDecimal) row[1])).toList();
    }

    private enum Type { REQUESTER, MANAGER, VENDOR, ORGANIZATION }
    private record Scope(Type type, Long id) { }
}

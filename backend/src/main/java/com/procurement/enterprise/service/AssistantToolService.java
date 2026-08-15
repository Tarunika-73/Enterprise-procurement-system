package com.procurement.enterprise.service;

import com.procurement.enterprise.entity.PurchaseOrder;
import com.procurement.enterprise.entity.User;
import com.procurement.enterprise.entity.Vendor;
import com.procurement.enterprise.repository.PurchaseOrderRepository;
import com.procurement.enterprise.repository.UserRepository;
import com.procurement.enterprise.repository.VendorRepository;
import com.procurement.enterprise.dto.response.ReportSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

/** Small, read-only operations where entity distinction matters; identity always comes from Spring Security. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssistantToolService {
    private final UserRepository users;
    private final VendorRepository vendors;
    private final PurchaseOrderRepository orders;
    private final ReportService reportService;

    public Optional<String> latestPurchaseOrder() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return Optional.empty();
        if (hasRole(auth, "ROLE_EMPLOYEE")) {
            User user = users.findByEmailAndIsDeletedFalse(auth.getName()).orElse(null);
            if (user == null) return Optional.empty();
            return orderResult(orders.findByPurchaseRequest_Requester_IdAndIsDeletedFalseOrderByCreatedAtDesc(user.getId(), PageRequest.of(0, 1)).stream().findFirst());
        }
        if (hasRole(auth, "ROLE_VENDOR")) {
            Vendor vendor = vendors.findByEmailAndIsDeletedFalse(auth.getName()).orElse(null);
            if (vendor == null) return Optional.empty();
            return orderResult(orders.findByVendorIdAndIsDeletedFalse(vendor.getId(), PageRequest.of(0, 1)).stream().findFirst());
        }
        return Optional.empty();
    }
    public Optional<String> myManager() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !hasRole(auth, "ROLE_EMPLOYEE")) return Optional.empty();
        User employee = users.findByEmailAndIsDeletedFalse(auth.getName()).orElse(null);
        if (employee == null || employee.getDepartment() == null || employee.getDepartment().getManager() == null)
            return Optional.of("No manager is assigned to your department.");
        User manager = employee.getDepartment().getManager();
        return Optional.of("Your department manager is " + manager.getFirstName() + " " + manager.getLastName() + ".");
    }
    public Optional<String> myReport() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !hasRole(auth, "ROLE_EMPLOYEE")) return Optional.empty();
        ReportSummaryResponse summary = reportService.getSummary(); // Existing service enforces self scope from SecurityContext.
        if (!"SELF".equals(summary.getScope())) return Optional.empty();
        long pending = Math.max(0, summary.getTotalRequests() - summary.getApprovedRequests());
        return Optional.of("Here is your procurement summary: Purchase Requests: " + summary.getTotalRequests()
                + "; Pending or in progress: " + pending + "; Approved or closed: " + summary.getApprovedRequests()
                + "; Purchase Orders: " + summary.getPurchaseOrders() + ".");
    }
    private Optional<String> orderResult(Optional<PurchaseOrder> order) {
        return order.map(value -> "Your latest purchase order, " + value.getPurchaseOrderNumber() + ", is " + value.getStatus().name().toLowerCase().replace('_', ' ') + ".")
                .or(() -> Optional.of("You do not have any purchase orders yet."));
    }
    private boolean hasRole(Authentication auth, String role) { return auth.getAuthorities().stream().anyMatch(a -> role.equals(a.getAuthority())); }
}

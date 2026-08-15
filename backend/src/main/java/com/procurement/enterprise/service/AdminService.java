package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {

    AdminDashboardStatsResponse getDashboardStats();

    // Users
    Page<UserResponse> getAllUsers(Pageable pageable);
    UserResponse getUserById(Long id);
    UserResponse activateUser(Long id, Long adminId);
    UserResponse deactivateUser(Long id, Long adminId);
    void deleteUser(Long id, Long adminId);

    // Vendors
    Page<VendorResponse> getAllVendors(Pageable pageable);
    VendorResponse getVendorById(Long id);
    VendorResponse activateVendor(Long id);
    VendorResponse deactivateVendor(Long id);

    // Departments
    Page<DepartmentResponse> getAllDepartments(Pageable pageable);

    // Products
    Page<ProductResponse> getAllProducts(Pageable pageable);

    // Procurement monitoring
    Page<PurchaseRequestResponse> getAllPurchaseRequests(Pageable pageable);
    Page<PurchaseOrderResponse> getAllPurchaseOrders(Pageable pageable);

    // Finance monitoring
    Page<InvoiceResponse> getAllInvoices(Pageable pageable);
    Page<PaymentResponse> getAllPayments(Pageable pageable);

    // Audit
    Page<AuditLogResponse> getAuditLogs(Pageable pageable);
}

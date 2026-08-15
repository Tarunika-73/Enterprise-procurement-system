package com.procurement.enterprise.controller;

import com.procurement.enterprise.dto.response.*;
import com.procurement.enterprise.dto.request.CreateDepartmentRequest;
import com.procurement.enterprise.dto.request.CreateProductRequest;
import com.procurement.enterprise.dto.request.UpdateDepartmentRequest;
import com.procurement.enterprise.dto.request.UpdateProductRequest;
import com.procurement.enterprise.exception.UnauthorizedException;
import com.procurement.enterprise.repository.UserRepository;
import com.procurement.enterprise.service.AdminService;
import com.procurement.enterprise.service.DepartmentService;
import com.procurement.enterprise.service.ProductService;
import com.procurement.enterprise.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;
    private final DepartmentService departmentService;
    private final ProductService productService;

    // ── Dashboard ──────────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardStatsResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(
                "Dashboard stats fetched successfully.", adminService.getDashboardStats()));
    }

    // ── Users ──────────────────────────────────────────────────────────────────

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getUsers(
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                "Users fetched successfully.", adminService.getAllUsers(pageable)));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "User fetched successfully.", adminService.getUserById(id)));
    }

    @PutMapping("/users/{id}/activate")
    public ResponseEntity<ApiResponse<UserResponse>> activateUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "User activated successfully.", adminService.activateUser(id, getCurrentAdminId())));
    }

    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<ApiResponse<UserResponse>> deactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "User deactivated successfully.", adminService.deactivateUser(id, getCurrentAdminId())));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id, getCurrentAdminId());
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully.", null));
    }

    // ── Vendors ────────────────────────────────────────────────────────────────

    @GetMapping("/vendors")
    public ResponseEntity<ApiResponse<Page<VendorResponse>>> getVendors(
            @ParameterObject
            @PageableDefault(size = 10, sort = "vendorName") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                "Vendors fetched successfully.", adminService.getAllVendors(pageable)));
    }

    @GetMapping("/vendors/{id}")
    public ResponseEntity<ApiResponse<VendorResponse>> getVendor(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Vendor fetched successfully.", adminService.getVendorById(id)));
    }

    @PutMapping("/vendors/{id}/activate")
    public ResponseEntity<ApiResponse<VendorResponse>> activateVendor(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Vendor activated successfully.", adminService.activateVendor(id)));
    }

    @PutMapping("/vendors/{id}/deactivate")
    public ResponseEntity<ApiResponse<VendorResponse>> deactivateVendor(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Vendor deactivated successfully.", adminService.deactivateVendor(id)));
    }

    // ── Departments ────────────────────────────────────────────────────────────

    @GetMapping("/departments")
    public ResponseEntity<ApiResponse<Page<DepartmentResponse>>> getDepartments(
            @ParameterObject
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                "Departments fetched successfully.", adminService.getAllDepartments(pageable)));
    }

    @PostMapping("/departments")
    public ResponseEntity<ApiResponse<DepartmentResponse>> createDepartment(
            @Valid @RequestBody CreateDepartmentRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.success(
                "Department created successfully.", departmentService.create(request)));
    }

    @PutMapping("/departments/{id}")
    public ResponseEntity<ApiResponse<DepartmentResponse>> updateDepartment(
            @PathVariable Long id, @Valid @RequestBody UpdateDepartmentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Department updated successfully.", departmentService.update(id, request)));
    }

    @DeleteMapping("/departments/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(@PathVariable Long id) {
        departmentService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Department deleted successfully.", null));
    }

    // ── Products ───────────────────────────────────────────────────────────────

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProducts(
            @ParameterObject
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                "Products fetched successfully.", adminService.getAllProducts(pageable)));
    }

    @PostMapping("/products")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.success(
                "Product created successfully.", productService.create(request)));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id, @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Product updated successfully.", productService.update(id, request)));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully.", null));
    }

    // ── Purchase Requests ──────────────────────────────────────────────────────

    @GetMapping("/purchase-requests")
    public ResponseEntity<ApiResponse<Page<PurchaseRequestResponse>>> getPurchaseRequests(
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                "Purchase requests fetched successfully.", adminService.getAllPurchaseRequests(pageable)));
    }

    // ── Purchase Orders ────────────────────────────────────────────────────────

    @GetMapping("/purchase-orders")
    public ResponseEntity<ApiResponse<Page<PurchaseOrderResponse>>> getPurchaseOrders(
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                "Purchase orders fetched successfully.", adminService.getAllPurchaseOrders(pageable)));
    }

    // ── Invoices ───────────────────────────────────────────────────────────────

    @GetMapping("/invoices")
    public ResponseEntity<ApiResponse<Page<InvoiceResponse>>> getInvoices(
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                "Invoices fetched successfully.", adminService.getAllInvoices(pageable)));
    }

    // ── Payments ───────────────────────────────────────────────────────────────

    @GetMapping("/payments")
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> getPayments(
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                "Payments fetched successfully.", adminService.getAllPayments(pageable)));
    }

    // ── Audit Logs ─────────────────────────────────────────────────────────────

    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> getAuditLogs(
            @ParameterObject
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                "Audit logs fetched successfully.", adminService.getAuditLogs(pageable)));
    }

    // ── Helper ─────────────────────────────────────────────────────────────────

    private Long getCurrentAdminId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("Authentication required.");
        }
        return userRepository.findByEmailAndIsDeletedFalse(auth.getName())
                .orElseThrow(() -> new UnauthorizedException("Admin user not found."))
                .getId();
    }
}

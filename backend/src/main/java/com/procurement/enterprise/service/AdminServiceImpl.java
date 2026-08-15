package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.response.*;
import com.procurement.enterprise.entity.*;
import com.procurement.enterprise.enums.InvoiceStatus;
import com.procurement.enterprise.enums.PaymentStatus;
import com.procurement.enterprise.enums.PurchaseOrderStatus;
import com.procurement.enterprise.enums.PurchaseRequestStatus;
import com.procurement.enterprise.exception.ForbiddenException;
import com.procurement.enterprise.exception.ResourceNotFoundException;
import com.procurement.enterprise.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final DepartmentRepository departmentRepository;
    private final ProductRepository productRepository;
    private final PurchaseRequestRepository purchaseRequestRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final AuditLogRepository auditLogRepository;

    // ── Dashboard ──────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardStatsResponse getDashboardStats() {
        long totalUsers    = userRepository.findAllByIsDeletedFalse(Pageable.unpaged()).getTotalElements();
        long activeUsers   = userRepository.findByIsActiveAndIsDeletedFalse(true, Pageable.unpaged()).getTotalElements();

        long totalVendors  = vendorRepository.countByIsDeletedFalse();
        long activeVendors = vendorRepository.countByIsActiveTrueAndIsDeletedFalse();

        long totalPR    = purchaseRequestRepository.countByIsDeletedFalse();
        long pendingPR  = purchaseRequestRepository.countByStatusAndIsDeletedFalse(PurchaseRequestStatus.PENDING);
        long approvedPR = purchaseRequestRepository.countByStatusAndIsDeletedFalse(PurchaseRequestStatus.APPROVED);
        long rejectedPR = purchaseRequestRepository.countByStatusAndIsDeletedFalse(PurchaseRequestStatus.REJECTED);

        long totalPO   = purchaseOrderRepository.countByIsDeletedFalse();
        long pendingPO = purchaseOrderRepository.countByStatusAndIsDeletedFalse(PurchaseOrderStatus.CREATED);

        long totalInvoices     = invoiceRepository.findAllByIsDeletedFalse(Pageable.unpaged()).getTotalElements();
        long pendingPayments   = paymentRepository.countByStatusAndIsDeletedFalse(PaymentStatus.PENDING);
        long completedPayments = paymentRepository.countByStatusAndIsDeletedFalse(PaymentStatus.PAID);

        return AdminDashboardStatsResponse.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .inactiveUsers(totalUsers - activeUsers)
                .totalVendors(totalVendors)
                .activeVendors(activeVendors)
                .totalPurchaseRequests(totalPR)
                .pendingPurchaseRequests(pendingPR)
                .approvedPurchaseRequests(approvedPR)
                .rejectedPurchaseRequests(rejectedPR)
                .totalPurchaseOrders(totalPO)
                .pendingPurchaseOrders(pendingPO)
                .totalInvoices(totalInvoices)
                .pendingPayments(pendingPayments)
                .completedPayments(completedPayments)
                .build();
    }

    // ── Users ──────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAllByIsDeletedFalse(pageable).map(this::mapUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return mapUser(findUser(id));
    }

    @Override
    @Transactional
    public UserResponse activateUser(Long id, Long adminId) {
        User user = findUser(id);
        user.setIsActive(true);
        return mapUser(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse deactivateUser(Long id, Long adminId) {
        if (id.equals(adminId)) {
            throw new ForbiddenException("You cannot deactivate your own account.");
        }
        User user = findUser(id);
        user.setIsActive(false);
        return mapUser(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long id, Long adminId) {
        if (id.equals(adminId)) {
            throw new ForbiddenException("You cannot delete your own account.");
        }
        User user = findUser(id);
        user.setIsDeleted(true);
        user.setIsActive(false);
        userRepository.save(user);
    }

    // ── Vendors ────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Page<VendorResponse> getAllVendors(Pageable pageable) {
        return vendorRepository.findAllByIsDeletedFalse(pageable).map(this::mapVendor);
    }

    @Override
    @Transactional(readOnly = true)
    public VendorResponse getVendorById(Long id) {
        return mapVendor(findVendor(id));
    }

    @Override
    @Transactional
    public VendorResponse activateVendor(Long id) {
        Vendor vendor = findVendor(id);
        vendor.setIsActive(true);
        return mapVendor(vendorRepository.save(vendor));
    }

    @Override
    @Transactional
    public VendorResponse deactivateVendor(Long id) {
        Vendor vendor = findVendor(id);
        vendor.setIsActive(false);
        return mapVendor(vendorRepository.save(vendor));
    }

    // ── Departments ────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Page<DepartmentResponse> getAllDepartments(Pageable pageable) {
        return departmentRepository.findAllByIsDeletedFalse(pageable).map(this::mapDepartment);
    }

    // ── Products ───────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAllByIsDeletedFalse(pageable).map(this::mapProduct);
    }

    // ── Procurement Monitoring ─────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Page<PurchaseRequestResponse> getAllPurchaseRequests(Pageable pageable) {
        return purchaseRequestRepository.findAllByIsDeletedFalse(pageable).map(this::mapPurchaseRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PurchaseOrderResponse> getAllPurchaseOrders(Pageable pageable) {
        return purchaseOrderRepository.findAllByIsDeletedFalse(pageable).map(this::mapPurchaseOrder);
    }

    // ── Finance Monitoring ─────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceResponse> getAllInvoices(Pageable pageable) {
        return invoiceRepository.findAllByIsDeletedFalse(pageable).map(this::mapInvoice);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponse> getAllPayments(Pageable pageable) {
        return paymentRepository.findAllByIsDeletedFalse(pageable).map(this::mapPayment);
    }

    // ── Audit Logs ─────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAuditLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByCreatedAtDesc(pageable).map(this::mapAuditLog);
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    private User findUser(Long id) {
        return userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    private Vendor findVendor(Long id) {
        return vendorRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", id));
    }

    // ── Mappers ────────────────────────────────────────────────────────────────

    private UserResponse mapUser(User u) {
        return UserResponse.builder()
                .id(u.getId())
                .employeeId(u.getEmployeeId())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .email(u.getEmail())
                .roleId(u.getRole() != null ? u.getRole().getId() : null)
                .roleName(u.getRole() != null ? u.getRole().getName() : null)
                .departmentId(u.getDepartment() != null ? u.getDepartment().getId() : null)
                .departmentName(u.getDepartment() != null ? u.getDepartment().getName() : null)
                .isActive(u.getIsActive())
                .createdAt(u.getCreatedAt())
                .updatedAt(u.getUpdatedAt())
                .build();
    }

    private VendorResponse mapVendor(Vendor v) {
        return VendorResponse.builder()
                .id(v.getId())
                .vendorName(v.getVendorName())
                .contactName(v.getContactName())
                .email(v.getEmail())
                .phone(v.getPhone())
                .address(v.getAddress())
                .gstNumber(v.getGstNumber())
                .isActive(v.getIsActive())
                .createdAt(v.getCreatedAt())
                .updatedAt(v.getUpdatedAt())
                .build();
    }

    private DepartmentResponse mapDepartment(Department d) {
        User mgr = d.getManager();
        return DepartmentResponse.builder()
                .id(d.getId())
                .name(d.getName())
                .code(d.getCode())
                .managerId(mgr != null ? mgr.getId() : null)
                .managerName(mgr != null ? mgr.getFirstName() + " " + mgr.getLastName() : null)
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }

    private ProductResponse mapProduct(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .sku(p.getSku())
                .name(p.getName())
                .description(p.getDescription())
                .categoryId(p.getCategory() != null ? p.getCategory().getId() : null)
                .categoryName(p.getCategory() != null ? p.getCategory().getName() : null)
                .departmentId(p.getDepartment() != null ? p.getDepartment().getId() : null)
                .departmentName(p.getDepartment() != null ? p.getDepartment().getName() : null)
                .availableQuantity(p.getAvailableQuantity())
                .isActive(p.getIsActive())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    private PurchaseRequestResponse mapPurchaseRequest(PurchaseRequest r) {
        User requester = r.getRequester();
        Department dept = r.getDepartment();
        return PurchaseRequestResponse.builder()
                .id(r.getId())
                .requestNumber(r.getRequestNumber())
                .title(r.getTitle())
                .requesterId(requester != null ? requester.getId() : null)
                .requesterName(requester != null ? requester.getFirstName() + " " + requester.getLastName() : null)
                .departmentId(dept != null ? dept.getId() : null)
                .departmentName(dept != null ? dept.getName() : null)
                .priority(r.getPriority())
                .status(r.getStatus())
                .approvalStatus(r.getStatus())
                .totalAmount(r.getTotalAmount())
                .approvalDate(r.getApprovalDate())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }

    private PurchaseOrderResponse mapPurchaseOrder(PurchaseOrder po) {
        return PurchaseOrderResponse.builder()
                .id(po.getId())
                .purchaseOrderNumber(po.getPurchaseOrderNumber())
                .purchaseRequestId(po.getPurchaseRequest() != null ? po.getPurchaseRequest().getId() : null)
                .requestNumber(po.getPurchaseRequest() != null ? po.getPurchaseRequest().getRequestNumber() : null)
                .vendorId(po.getVendor() != null ? po.getVendor().getId() : null)
                .vendorName(po.getVendor() != null ? po.getVendor().getVendorName() : null)
                .vendorEmail(po.getVendor() != null ? po.getVendor().getEmail() : null)
                .status(po.getStatus())
                .totalAmount(po.getTotalAmount())
                .expectedDeliveryDate(po.getExpectedDeliveryDate())
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .build();
    }

    private InvoiceResponse mapInvoice(Invoice inv) {
        return InvoiceResponse.builder()
                .id(inv.getId())
                .invoiceNumber(inv.getInvoiceNumber())
                .receiptId(inv.getReceipt() != null ? inv.getReceipt().getId() : null)
                .vendorId(inv.getVendor() != null ? inv.getVendor().getId() : null)
                .vendorName(inv.getVendor() != null ? inv.getVendor().getVendorName() : null)
                .invoiceDate(inv.getInvoiceDate())
                .dueDate(inv.getDueDate())
                .totalAmount(inv.getTotalAmount())
                .status(inv.getStatus())
                .createdAt(inv.getCreatedAt())
                .updatedAt(inv.getUpdatedAt())
                .build();
    }

    private PaymentResponse mapPayment(Payment p) {
        Invoice inv = p.getInvoice();
        return PaymentResponse.builder()
                .id(p.getId())
                .paymentReference(p.getPaymentReference())
                .invoiceId(inv != null ? inv.getId() : null)
                .invoiceNumber(inv != null ? inv.getInvoiceNumber() : null)
                .vendorId(inv != null && inv.getVendor() != null ? inv.getVendor().getId() : null)
                .vendorName(inv != null && inv.getVendor() != null ? inv.getVendor().getVendorName() : null)
                .amountPaid(p.getAmountPaid())
                .paymentDate(p.getPaymentDate())
                .paymentMethod(p.getPaymentMethod())
                .remarks(p.getRemarks())
                .status(p.getStatus())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    private AuditLogResponse mapAuditLog(AuditLog a) {
        User u = a.getUser();
        return AuditLogResponse.builder()
                .id(a.getId())
                .userId(u != null ? u.getId() : null)
                .userName(u != null ? u.getFirstName() + " " + u.getLastName() : null)
                .action(a.getAction())
                .tableName(a.getTableName())
                .recordId(a.getRecordId())
                .oldValue(a.getOldValue())
                .newValue(a.getNewValue())
                .ipAddress(a.getIpAddress())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }
}

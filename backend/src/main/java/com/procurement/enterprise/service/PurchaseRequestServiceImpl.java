package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.request.CreatePurchaseRequestRequest;
import com.procurement.enterprise.dto.request.ManagerDecisionRequest;
import com.procurement.enterprise.dto.response.EmployeeDashboardStatsResponse;
import com.procurement.enterprise.dto.response.ManagerDashboardStatsResponse;
import com.procurement.enterprise.dto.response.PurchaseRequestItemResponse;
import com.procurement.enterprise.dto.response.PurchaseRequestResponse;
import com.procurement.enterprise.dto.response.RequestTimelineEntry;
import com.procurement.enterprise.entity.Department;
import com.procurement.enterprise.entity.Product;
import com.procurement.enterprise.entity.PurchaseOrder;
import com.procurement.enterprise.entity.PurchaseRequest;
import com.procurement.enterprise.entity.PurchaseRequestItem;
import com.procurement.enterprise.entity.User;
import com.procurement.enterprise.entity.ApprovalHistory;
import com.procurement.enterprise.enums.ApprovalActionTaken;
import com.procurement.enterprise.enums.PurchaseOrderStatus;
import com.procurement.enterprise.enums.PurchaseRequestStatus;
import com.procurement.enterprise.enums.RequestPriority;
import com.procurement.enterprise.exception.InvalidRequestException;
import com.procurement.enterprise.exception.ForbiddenException;
import com.procurement.enterprise.exception.ResourceNotFoundException;
import com.procurement.enterprise.exception.UnauthorizedException;
import com.procurement.enterprise.repository.NotificationRepository;
import com.procurement.enterprise.repository.ProductRepository;
import com.procurement.enterprise.repository.PurchaseOrderRepository;
import com.procurement.enterprise.repository.PurchaseRequestItemRepository;
import com.procurement.enterprise.repository.PurchaseRequestRepository;
import com.procurement.enterprise.repository.UserRepository;
import com.procurement.enterprise.repository.ApprovalHistoryRepository;
import com.procurement.enterprise.util.Constants;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PurchaseRequestServiceImpl implements PurchaseRequestService {

    private static final Logger log = LoggerFactory.getLogger(PurchaseRequestServiceImpl.class);

    private final PurchaseRequestRepository purchaseRequestRepository;
    private final PurchaseRequestItemRepository purchaseRequestItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;
    private final ApprovalHistoryRepository approvalHistoryRepository;

    // ── Create ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public PurchaseRequestResponse create(CreatePurchaseRequestRequest request) {
        User requester = getCurrentUser();
        Product product = productRepository.findByIdAndIsDeletedFalse(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", request.getProductId()));

        if (!Boolean.TRUE.equals(product.getIsActive())) {
            throw new InvalidRequestException("Selected product is not available for procurement.");
        }

        int availableQty = product.getAvailableQuantity() != null ? product.getAvailableQuantity() : 100;
        if (availableQty > 0 && request.getQuantity() > availableQty) {
            throw new InvalidRequestException(
                    "Requested quantity exceeds available stock (" + availableQty + ").");
        }

        Department department = product.getDepartment();
        if (department == null || Boolean.TRUE.equals(department.getIsDeleted())) {
            throw new InvalidRequestException("Selected product does not have a valid department assigned.");
        }
        User manager = resolveDepartmentManager(department);
        if (manager == null) {
            throw new InvalidRequestException("No manager is assigned to the product's department.");
        }

        BigDecimal totalAmount = request.getUnitPrice()
                .multiply(BigDecimal.valueOf(request.getQuantity()));

        PurchaseRequest purchaseRequest = PurchaseRequest.builder()
                .requestNumber(generateRequestNumber())
                .requester(requester)
                .department(department)
                .title(request.getTitle().trim())
                .justification(request.getJustification().trim())
                .priority(request.getPriority())
                .expectedDeliveryDate(request.getExpectedDeliveryDate())
                .status(PurchaseRequestStatus.PENDING)
                .totalAmount(totalAmount)
                .manager(manager)
                .currentApprover(manager)
                .isDeleted(false)
                .build();

        PurchaseRequest saved = purchaseRequestRepository.save(purchaseRequest);

        PurchaseRequestItem item = PurchaseRequestItem.builder()
                .purchaseRequest(saved)
                .product(product)
                .quantity(request.getQuantity())
                .estimatedPrice(request.getUnitPrice())
                .isDeleted(false)
                .build();
        purchaseRequestItemRepository.save(item);

        notificationService.createNotification(manager, com.procurement.enterprise.enums.NotificationType.SYSTEM,
                "Purchase request submitted", "Purchase request " + saved.getRequestNumber() + " requires your approval.");
        auditLogService.record("CREATE", "purchase_requests", saved.getId(), null, "status=PENDING");

        log.info("Purchase request {} assigned to manager {}", saved.getRequestNumber(), manager.getId());
        return mapToResponse(saved, true);
    }

    @Override
    @Transactional
    public PurchaseRequestResponse updatePendingRequest(Long id, CreatePurchaseRequestRequest request) {
        User requester = getCurrentUser();
        PurchaseRequest purchaseRequest = findActive(id);
        if (!Objects.equals(purchaseRequest.getRequester().getId(), requester.getId())) {
            throw new ForbiddenException("You can only edit your own purchase requests.");
        }
        if (purchaseRequest.getStatus() != PurchaseRequestStatus.PENDING) {
            throw new InvalidRequestException("Only requests awaiting manager approval can be edited.");
        }

        Product product = productRepository.findByIdAndIsDeletedFalse(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", request.getProductId()));
        if (!Boolean.TRUE.equals(product.getIsActive())) {
            throw new InvalidRequestException("Selected product is not available for procurement.");
        }
        int availableQty = product.getAvailableQuantity() != null ? product.getAvailableQuantity() : 100;
        if (availableQty > 0 && request.getQuantity() > availableQty) {
            throw new InvalidRequestException("Requested quantity exceeds available stock (" + availableQty + ").");
        }

        purchaseRequest.setTitle(request.getTitle().trim());
        purchaseRequest.setJustification(request.getJustification().trim());
        purchaseRequest.setPriority(request.getPriority());
        purchaseRequest.setExpectedDeliveryDate(request.getExpectedDeliveryDate());
        purchaseRequest.setTotalAmount(request.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity())));

        PurchaseRequestItem item = purchaseRequestItemRepository
                .findByPurchaseRequestIdAndIsDeletedFalse(purchaseRequest.getId()).stream()
                .findFirst()
                .orElseThrow(() -> new InvalidRequestException("Purchase request item was not found."));
        item.setProduct(product);
        item.setQuantity(request.getQuantity());
        item.setEstimatedPrice(request.getUnitPrice());
        purchaseRequestItemRepository.save(item);

        PurchaseRequest saved = purchaseRequestRepository.save(purchaseRequest);
        auditLogService.record("UPDATE", "purchase_requests", saved.getId(), null, "status=PENDING");
        return mapToResponse(saved, true);
    }

    // ── Read ───────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public PurchaseRequestResponse getById(Long id) {
        PurchaseRequest request = findActive(id);
        ensureCanView(request);
        return mapToResponse(request, true);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PurchaseRequestResponse> getMyRequests(Pageable pageable) {
        User requester = getCurrentUser();
        return purchaseRequestRepository
                .findByRequesterIdAndIsDeletedFalse(requester.getId(), pageable)
                .map(pr -> mapToResponse(pr, false));
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeDashboardStatsResponse getMyDashboardStats() {
        User requester = getCurrentUser();
        Long userId = requester.getId();

        List<PurchaseRequestResponse> recent = purchaseRequestRepository
                .findByRequesterIdAndIsDeletedFalseOrderByCreatedAtDesc(userId, PageRequest.of(0, 5))
                .map(pr -> mapToResponse(pr, false))
                .getContent();

        return EmployeeDashboardStatsResponse.builder()
                .totalRequests(purchaseRequestRepository.countByRequesterIdAndIsDeletedFalse(userId))
                .pendingRequests(purchaseRequestRepository
                        .countByRequesterIdAndStatusAndIsDeletedFalse(userId, PurchaseRequestStatus.PENDING))
                .closedRequests(purchaseRequestRepository
                        .countByRequesterIdAndStatusAndIsDeletedFalse(userId, PurchaseRequestStatus.CLOSED))
                .approvedRequests(purchaseRequestRepository
                        .countByRequesterIdAndStatusAndIsDeletedFalse(userId, PurchaseRequestStatus.APPROVED))
                .rejectedRequests(purchaseRequestRepository
                        .countByRequesterIdAndStatusAndIsDeletedFalse(userId, PurchaseRequestStatus.REJECTED))
                .unreadNotifications(notificationRepository.countByUserIdAndIsReadFalseAndIsDeletedFalse(userId))
                .recentRequests(recent)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PurchaseRequestResponse> getManagerInbox(Pageable pageable) {
        User manager = requireManager();
        return purchaseRequestRepository
                .findByManagerIdAndIsDeletedFalse(manager.getId(), pageable)
                .map(pr -> mapToResponse(pr, false));
    }

    @Override
    @Transactional(readOnly = true)
    public ManagerDashboardStatsResponse getManagerDashboardStats() {
        User manager = requireManager();
        Long managerId = manager.getId();

        List<PurchaseRequestResponse> recent = purchaseRequestRepository
                .findByManagerIdAndIsDeletedFalseOrderByCreatedAtDesc(managerId, PageRequest.of(0, 5))
                .map(pr -> mapToResponse(pr, false))
                .getContent();

        return ManagerDashboardStatsResponse.builder()
                .totalAssignedRequests(purchaseRequestRepository.countByManagerIdAndIsDeletedFalse(managerId))
                .pendingRequests(purchaseRequestRepository
                        .countByManagerIdAndStatusAndIsDeletedFalse(managerId, PurchaseRequestStatus.PENDING))
                .approvedRequests(purchaseRequestRepository
                        .countByManagerIdAndStatusAndIsDeletedFalse(managerId, PurchaseRequestStatus.APPROVED))
                .rejectedRequests(purchaseRequestRepository
                        .countByManagerIdAndStatusAndIsDeletedFalse(managerId, PurchaseRequestStatus.REJECTED))
                .returnedRequests(purchaseRequestRepository
                        .countByManagerIdAndStatusAndIsDeletedFalse(
                                managerId, PurchaseRequestStatus.RETURNED_FOR_MODIFICATION))
                .urgentRequests(purchaseRequestRepository
                        .countByManagerIdAndPriorityAndIsDeletedFalse(managerId, RequestPriority.URGENT))
                .recentRequests(recent)
                .build();
    }

    // ── Manager Actions ────────────────────────────────────────────────────────

    @Override
    @Transactional
    public PurchaseRequestResponse approve(Long id, ManagerDecisionRequest decision) {
        PurchaseRequest request = getManagedPendingRequest(id);
        User manager = requireManager();
        request.setStatus(PurchaseRequestStatus.APPROVED);
        request.setManagerRemarks(normalizeRemarks(decision));
        request.setApprovalDate(LocalDateTime.now());

        User procurementOfficer = userRepository.findActiveProcurementOfficers().stream()
                .findFirst()
                .orElse(null);
        if (procurementOfficer != null) {
            request.setCurrentApprover(procurementOfficer);
        }

        PurchaseRequest saved = purchaseRequestRepository.save(request);
        saveManagerApprovalHistory(saved, manager, ApprovalActionTaken.APPROVED, saved.getManagerRemarks());
        notificationService.createNotification(saved.getRequester(), com.procurement.enterprise.enums.NotificationType.SYSTEM,
                "Purchase request approved", "Your request " + saved.getRequestNumber() + " was approved.");
        for (User officer : userRepository.findActiveProcurementOfficers()) notificationService.createNotification(officer,
                com.procurement.enterprise.enums.NotificationType.SYSTEM, "Approved request ready", saved.getRequestNumber() + " is ready for purchase order creation.");
        auditLogService.record("APPROVE", "purchase_requests", saved.getId(), "status=PENDING", "status=APPROVED");
        log.info("Manager approved request {}", saved.getRequestNumber());
        return mapToResponse(saved, true);
    }

    @Override
    @Transactional
    public PurchaseRequestResponse reject(Long id, ManagerDecisionRequest decision) {
        requireRemarks(decision, "Remarks are required when rejecting a request.");
        PurchaseRequest request = getManagedPendingRequest(id);
        User manager = requireManager();
        request.setStatus(PurchaseRequestStatus.REJECTED);
        request.setManagerRemarks(decision.getRemarks().trim());
        request.setApprovalDate(LocalDateTime.now());
        PurchaseRequest saved = purchaseRequestRepository.save(request);
        saveManagerApprovalHistory(saved, manager, ApprovalActionTaken.REJECTED, saved.getManagerRemarks());
        notificationService.createNotification(saved.getRequester(), com.procurement.enterprise.enums.NotificationType.SYSTEM,
                "Purchase request rejected", "Your request " + saved.getRequestNumber() + " was rejected.");
        auditLogService.record("REJECT", "purchase_requests", saved.getId(), "status=PENDING", "status=REJECTED");
        log.info("Manager rejected request {}", saved.getRequestNumber());
        return mapToResponse(saved, true);
    }

    @Override
    @Transactional
    public PurchaseRequestResponse returnForModification(Long id, ManagerDecisionRequest decision) {
        requireRemarks(decision, "Remarks are required when returning a request for modification.");
        PurchaseRequest request = getManagedPendingRequest(id);
        User manager = requireManager();
        request.setStatus(PurchaseRequestStatus.RETURNED_FOR_MODIFICATION);
        request.setManagerRemarks(decision.getRemarks().trim());
        request.setApprovalDate(LocalDateTime.now());
        request.setCurrentApprover(request.getRequester());
        PurchaseRequest saved = purchaseRequestRepository.save(request);
        saveManagerApprovalHistory(saved, manager, ApprovalActionTaken.RETURNED, saved.getManagerRemarks());
        notificationService.createNotification(saved.getRequester(), com.procurement.enterprise.enums.NotificationType.SYSTEM,
                "Purchase request returned", "Your request " + saved.getRequestNumber() + " was returned for modification.");
        auditLogService.record("RETURN", "purchase_requests", saved.getId(), "status=PENDING", "status=RETURNED_FOR_MODIFICATION");
        log.info("Manager returned request {} for modification", saved.getRequestNumber());
        return mapToResponse(saved, true);
    }

    // ── Assignment Preview ─────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public PurchaseRequestResponse getAssignmentPreview(Long productId) {
        User employee = getCurrentUser();
        Department employeeDepartment = employee.getDepartment();
        Department department = productId == null ? employeeDepartment : productRepository
                .findByIdAndIsDeletedFalse(productId)
                .map(Product::getDepartment)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
        if (department == null) {
            throw new InvalidRequestException(productId == null
                    ? "Logged-in user has no department assigned."
                    : "Selected product does not have a valid department assigned.");
        }
        User manager = resolveDepartmentManager(department);
        if (manager == null) {
            throw new InvalidRequestException(productId == null
                    ? "No manager is assigned for your department."
                    : "No manager is assigned to the product's department.");
        }

        return PurchaseRequestResponse.builder()
                .requesterId(employee.getId())
                .requesterName(employee.getFirstName() + " " + employee.getLastName())
                .employeeCode(employee.getEmployeeId())
                .departmentId(employeeDepartment != null ? employeeDepartment.getId() : null)
                .departmentName(employeeDepartment != null ? employeeDepartment.getName() : "Unassigned")
                .managerId(manager != null ? manager.getId() : null)
                .managerName(manager != null ? manager.getFirstName() + " " + manager.getLastName() : null)
                .currentApproverId(manager != null ? manager.getId() : null)
                .currentApproverName(manager != null ? manager.getFirstName() + " " + manager.getLastName() : null)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // ── Mapping ────────────────────────────────────────────────────────────────

    private PurchaseRequestResponse mapToResponse(PurchaseRequest request, boolean includeDetails) {
        List<PurchaseRequestItem> items = purchaseRequestItemRepository
                .findByPurchaseRequestIdAndIsDeletedFalse(request.getId());

        PurchaseRequestItem primary = items.isEmpty() ? null : items.get(0);

        List<PurchaseRequestItemResponse> itemResponses = items.stream()
                .map(item -> PurchaseRequestItemResponse.builder()
                        .id(item.getId())
                        .purchaseRequestId(request.getId())
                        .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                        .productName(item.getProduct() != null ? item.getProduct().getName() : null)
                        .productSku(item.getProduct() != null ? item.getProduct().getSku() : null)
                        .quantity(item.getQuantity())
                        .estimatedPrice(item.getEstimatedPrice())
                        .createdAt(item.getCreatedAt())
                        .updatedAt(item.getUpdatedAt())
                        .build())
                .toList();

        User requester = request.getRequester();
        User manager = request.getManager() != null ? request.getManager() : request.getCurrentApprover();
        User approver = request.getCurrentApprover();
        Department department = request.getDepartment();

        PurchaseRequestStatus displayStatus = request.getStatus();

PurchaseOrder po = purchaseOrderRepository
        .findByPurchaseRequestIdAndIsDeletedFalse(request.getId())
        .orElse(null);

if (po != null) {

    switch (po.getStatus()) {

        case CREATED,
             SENT,
             ACCEPTED -> displayStatus = PurchaseRequestStatus.APPROVED;

        case DELIVERED,
             CLOSED -> displayStatus = PurchaseRequestStatus.CLOSED;

        case CANCELLED -> displayStatus = PurchaseRequestStatus.CANCELLED;

        default -> {
        }
    }
}

        PurchaseRequestResponse.PurchaseRequestResponseBuilder builder = PurchaseRequestResponse.builder()
                .id(request.getId())
                .requestNumber(request.getRequestNumber())
                .title(request.getTitle())
                .requesterId(requester != null ? requester.getId() : null)
                .requesterName(requester != null ? requester.getFirstName() + " " + requester.getLastName() : null)
                .employeeCode(requester != null ? requester.getEmployeeId() : null)
                .departmentId(department != null ? department.getId() : null)
                .departmentName(department != null ? department.getName() : null)
                .justification(request.getJustification())
                .priority(request.getPriority())
                .expectedDeliveryDate(request.getExpectedDeliveryDate())
                .status(displayStatus)
                .approvalStatus(displayStatus)
                .totalAmount(request.getTotalAmount())
                .managerId(manager != null ? manager.getId() : null)
                .managerName(manager != null ? manager.getFirstName() + " " + manager.getLastName() : null)
                .currentApproverId(approver != null ? approver.getId() : null)
                .currentApproverName(approver != null ? approver.getFirstName() + " " + approver.getLastName() : null)
                .managerRemarks(request.getManagerRemarks())
                .approvalDate(request.getApprovalDate())
                .productId(primary != null && primary.getProduct() != null ? primary.getProduct().getId() : null)
                .productName(primary != null && primary.getProduct() != null ? primary.getProduct().getName() : null)
                .productSku(primary != null && primary.getProduct() != null ? primary.getProduct().getSku() : null)
                .categoryName(primary != null && primary.getProduct() != null && primary.getProduct().getCategory() != null
                        ? primary.getProduct().getCategory().getName() : null)
                .quantity(primary != null ? primary.getQuantity() : null)
                .unitPrice(primary != null ? primary.getEstimatedPrice() : null)
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt());

        if (includeDetails) {
            builder.items(itemResponses);
            builder.timeline(buildTimeline(request));
        }

        return builder.build();
    }

    // ── Timeline ───────────────────────────────────────────────────────────────

    private List<RequestTimelineEntry> buildTimeline(PurchaseRequest request) {
        List<RequestTimelineEntry> timeline = new ArrayList<>();

        // Look up PO first so we can decide whether to show "Procurement Officer Pending"
        PurchaseOrder po = purchaseOrderRepository
                .findByPurchaseRequestIdAndIsDeletedFalse(request.getId())
                .orElse(null);

        // ── Stage 1: Request Submitted ─────────────────────────────────────────
        String requesterName = request.getRequester() != null
                ? request.getRequester().getFirstName() + " " + request.getRequester().getLastName()
                : "Employee";

        timeline.add(RequestTimelineEntry.builder()
                .stage("Request Submitted")
                .status("COMPLETED")
                .actorName(requesterName)
                .remarks(request.getJustification())
                .timestamp(request.getCreatedAt())
                .build());

        // ── Stage 2: Manager Approval ──────────────────────────────────────────
        User manager = request.getManager() != null ? request.getManager() : request.getCurrentApprover();
        String managerName = manager != null
                ? manager.getFirstName() + " " + manager.getLastName()
                : "Department Manager";

        PurchaseRequestStatus status = request.getStatus();

        switch (status) {
            case PENDING, SUBMITTED -> timeline.add(RequestTimelineEntry.builder()
                    .stage("Manager Approval")
                    .status("PENDING")
                    .actorName(managerName)
                    .remarks("Awaiting manager review")
                    .timestamp(request.getUpdatedAt())
                    .build());

            case APPROVED -> {
                timeline.add(RequestTimelineEntry.builder()
                        .stage("Manager Approval")
                        .status("APPROVED")
                        .actorName(managerName)
                        .remarks(request.getManagerRemarks() != null ? request.getManagerRemarks() : "Approved")
                        .timestamp(request.getApprovalDate() != null ? request.getApprovalDate() : request.getUpdatedAt())
                        .build());

                // Only show "Procurement Officer Pending" when no PO has been created yet
                if (po == null) {
                    String officerName = request.getCurrentApprover() != null
                            ? request.getCurrentApprover().getFirstName() + " " + request.getCurrentApprover().getLastName()
                            : "Procurement Officer";
                    timeline.add(RequestTimelineEntry.builder()
                            .stage("Procurement Officer")
                            .status("PENDING")
                            .actorName(officerName)
                            .remarks("Forwarded after manager approval")
                            .timestamp(request.getApprovalDate() != null ? request.getApprovalDate() : request.getUpdatedAt())
                            .build());
                }
            }

            case REJECTED -> timeline.add(RequestTimelineEntry.builder()
                    .stage("Manager Approval")
                    .status("REJECTED")
                    .actorName(managerName)
                    .remarks(request.getManagerRemarks() != null ? request.getManagerRemarks() : "Rejected")
                    .timestamp(request.getApprovalDate() != null ? request.getApprovalDate() : request.getUpdatedAt())
                    .build());

            case RETURNED_FOR_MODIFICATION -> timeline.add(RequestTimelineEntry.builder()
                    .stage("Manager Approval")
                    .status("RETURNED_FOR_MODIFICATION")
                    .actorName(managerName)
                    .remarks(request.getManagerRemarks())
                    .timestamp(request.getApprovalDate() != null ? request.getApprovalDate() : request.getUpdatedAt())
                    .build());

            default -> { /* no additional manager stage entry for other statuses */ }
        }

        // ── Stage 3+: Purchase Order stages (only when a PO exists) ───────────
        if (po == null) {
            return timeline;
        }

        PurchaseOrderStatus poStatus = po.getStatus();

        // Every PO path starts with "Purchase Order Created"
        RequestTimelineEntry poCreated = RequestTimelineEntry.builder()
                .stage("Purchase Order Created")
                .status("COMPLETED")
                .actorName("Procurement Officer")
                .remarks("Purchase Order " + po.getPurchaseOrderNumber() + " created successfully")
                .timestamp(po.getCreatedAt())
                .build();

        RequestTimelineEntry poSent = RequestTimelineEntry.builder()
                .stage("Purchase Order Sent")
                .status("COMPLETED")
                .actorName("Procurement Officer")
                .remarks("Purchase Order sent to vendor")
                .timestamp(po.getUpdatedAt())
                .build();

        RequestTimelineEntry vendorAccepted = RequestTimelineEntry.builder()
                .stage("Vendor Accepted Order")
                .status("COMPLETED")
                .actorName("Vendor")
                .remarks("Vendor accepted the Purchase Order")
                .timestamp(po.getUpdatedAt())
                .build();

        RequestTimelineEntry orderDelivered = RequestTimelineEntry.builder()
                .stage("Order Delivered")
                .status("COMPLETED")
                .actorName("Vendor")
                .remarks("Items delivered successfully")
                .timestamp(po.getUpdatedAt())
                .build();

        switch (poStatus) {
            case CREATED -> timeline.add(poCreated);

            case SENT -> {
                timeline.add(poCreated);
                timeline.add(poSent);
            }

            case ACCEPTED -> {
                timeline.add(poCreated);
                timeline.add(poSent);
                timeline.add(vendorAccepted);
            }

            case DELIVERED -> {
                timeline.add(poCreated);
                timeline.add(poSent);
                timeline.add(vendorAccepted);
                timeline.add(orderDelivered);
            }

            case CLOSED -> {
                timeline.add(poCreated);
                timeline.add(poSent);
                timeline.add(vendorAccepted);
                timeline.add(orderDelivered);
                timeline.add(RequestTimelineEntry.builder()
                        .stage("Procurement Completed")
                        .status("COMPLETED")
                        .actorName("Procurement Officer")
                        .remarks("Purchase Order closed successfully")
                        .timestamp(po.getUpdatedAt())
                        .build());
            }

            case REJECTED -> timeline.add(RequestTimelineEntry.builder()
                    .stage("Vendor Rejected Order")
                    .status("REJECTED")
                    .actorName("Vendor")
                    .remarks("Vendor rejected the Purchase Order")
                    .timestamp(po.getUpdatedAt())
                    .build());

            case CANCELLED -> timeline.add(RequestTimelineEntry.builder()
                    .stage("Purchase Order Cancelled")
                    .status("CANCELLED")
                    .actorName("Procurement Officer")
                    .remarks("Purchase Order cancelled")
                    .timestamp(po.getUpdatedAt())
                    .build());
        }

        return timeline;
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private PurchaseRequest getManagedPendingRequest(Long id) {
        User manager = requireManager();
        PurchaseRequest request = findActive(id);

        Long assignedManagerId = request.getManager() != null
                ? request.getManager().getId()
                : (request.getCurrentApprover() != null ? request.getCurrentApprover().getId() : null);

        if (!Objects.equals(assignedManagerId, manager.getId())) {
            throw new ForbiddenException("This request is not assigned to you.");
        }
        if (request.getStatus() != PurchaseRequestStatus.PENDING) {
            throw new InvalidRequestException("Only pending requests can be actioned.");
        }
        return request;
    }

    private void saveManagerApprovalHistory(PurchaseRequest request, User manager,
                                            ApprovalActionTaken action, String remarks) {
        approvalHistoryRepository.save(ApprovalHistory.builder()
                .purchaseRequest(request).actionBy(manager).actionTaken(action)
                .approvalLevel(1).remarks(remarks).isDeleted(false).build());
    }

    private User requireManager() {
        User user = getCurrentUser();
        if (!isManagerRole(user.getRole() != null ? user.getRole().getName() : null)) {
            throw new UnauthorizedException("Only department managers can access this resource.");
        }
        return user;
    }

    private User resolveDepartmentManager(Department department) {
        if (department.getManager() != null
                && Boolean.TRUE.equals(department.getManager().getIsActive())
                && !Boolean.TRUE.equals(department.getManager().getIsDeleted())) {
            return department.getManager();
        }
        return userRepository.findActiveManagersByDepartmentId(department.getId()).stream()
                .findFirst()
                .orElse(null);
    }

    private void ensureCanView(PurchaseRequest request) {
        User current = getCurrentUser();
        String role = current.getRole() != null ? current.getRole().getName() : "";
        boolean isOwner = request.getRequester().getId().equals(current.getId());
        boolean isAssignedManager = request.getManager() != null
                && Objects.equals(request.getManager().getId(), current.getId());
        boolean isAdmin = role.equalsIgnoreCase("Admin");
        boolean isProcurement = role.equalsIgnoreCase("Procurement Officer");

        if (isOwner || isAssignedManager || isAdmin || isProcurement) {
            return;
        }

        if (isManagerRole(role)) {
            throw new UnauthorizedException("Managers can only view requests assigned to them.");
        }

        throw new UnauthorizedException("You are not allowed to view this purchase request.");
    }

    private boolean isManagerRole(String roleName) {
        if (roleName == null) return false;
        String normalized = roleName.trim().toLowerCase(Locale.ROOT);
        return normalized.equals("manager") || normalized.equals("department manager");
    }

    private void requireRemarks(ManagerDecisionRequest decision, String message) {
        if (decision == null || decision.getRemarks() == null || decision.getRemarks().isBlank()) {
            throw new InvalidRequestException(message);
        }
    }

    private String normalizeRemarks(ManagerDecisionRequest decision) {
        if (decision == null || decision.getRemarks() == null || decision.getRemarks().isBlank()) {
            return "Approved";
        }
        return decision.getRemarks().trim();
    }

    private PurchaseRequest findActive(Long id) {
        return purchaseRequestRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Request", id));
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Authentication required.");
        }
        return userRepository.findByEmailAndIsDeletedFalse(authentication.getName())
                .orElseThrow(() -> new UnauthorizedException("Authenticated user not found."));
    }

    private String generateRequestNumber() {
        long next = purchaseRequestRepository.count() + 1;
        return String.format("%s%d-%04d", Constants.PR_PREFIX, Year.now().getValue(), next);
    }
}

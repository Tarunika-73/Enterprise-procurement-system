package com.procurement.enterprise.controller;
import org.springframework.transaction.annotation.Transactional;
import com.procurement.enterprise.dto.request.AssignVendorRequest;
import com.procurement.enterprise.dto.request.CreatePurchaseOrderRequest;
import com.procurement.enterprise.dto.response.ProcurementDashboardStatsResponse;
import com.procurement.enterprise.dto.response.PurchaseOrderResponse;
import com.procurement.enterprise.dto.response.PurchaseRequestResponse;
import com.procurement.enterprise.dto.response.VendorResponse;
import com.procurement.enterprise.entity.PurchaseOrder;
import com.procurement.enterprise.entity.PurchaseOrderItem;
import com.procurement.enterprise.entity.PurchaseRequest;
import com.procurement.enterprise.entity.PurchaseRequestItem;
import com.procurement.enterprise.entity.Vendor;
import com.procurement.enterprise.enums.PurchaseOrderStatus;
import com.procurement.enterprise.enums.PurchaseRequestStatus;
import com.procurement.enterprise.exception.DuplicateResourceException;
import com.procurement.enterprise.exception.InvalidRequestException;
import com.procurement.enterprise.exception.ResourceNotFoundException;
import com.procurement.enterprise.repository.DeliveryRepository;
import com.procurement.enterprise.repository.PurchaseOrderItemRepository;
import com.procurement.enterprise.repository.PurchaseOrderRepository;
import com.procurement.enterprise.repository.PurchaseRequestItemRepository;
import com.procurement.enterprise.repository.PurchaseRequestRepository;
import com.procurement.enterprise.repository.VendorRepository;
import com.procurement.enterprise.util.ApiResponse;
import com.procurement.enterprise.util.Constants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;

@RestController
@RequestMapping("/v1/procurement")
@PreAuthorize("hasRole('PROCUREMENT_OFFICER')")
@RequiredArgsConstructor
public class ProcurementOfficerController {

    private final PurchaseRequestRepository purchaseRequestRepository;
    private final PurchaseRequestItemRepository purchaseRequestItemRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final VendorRepository vendorRepository;

    // ── Dashboard Stats ────────────────────────────────────────────────────────

    @GetMapping("/dashboard-stats")
    @Transactional(readOnly = true)
        public ResponseEntity<ApiResponse<ProcurementDashboardStatsResponse>> getDashboardStats() {
        long approvedRequests = purchaseRequestRepository
                .findByStatusAndIsDeletedFalse(PurchaseRequestStatus.APPROVED, Pageable.unpaged())
                .getTotalElements();

        long totalPOs = purchaseOrderRepository
                .findAllByIsDeletedFalse(Pageable.unpaged())
                .getTotalElements();

        long activeVendors = vendorRepository
                .findByIsActiveAndIsDeletedFalse(true, Pageable.unpaged())
                .getTotalElements();

        long pendingDeliveries = purchaseOrderRepository
                .findByStatusAndIsDeletedFalse(PurchaseOrderStatus.SENT, Pageable.unpaged())
                .getTotalElements()
                + purchaseOrderRepository
                .findByStatusAndIsDeletedFalse(PurchaseOrderStatus.ACCEPTED, Pageable.unpaged())
                .getTotalElements();

        ProcurementDashboardStatsResponse stats = ProcurementDashboardStatsResponse.builder()
                .approvedRequests(approvedRequests)
                .totalPurchaseOrders(totalPOs)
                .activeVendors(activeVendors)
                .pendingDeliveries(pendingDeliveries)
                .build();

        return ResponseEntity.ok(ApiResponse.success("Dashboard stats fetched successfully.", stats));
    }

    // ── Approved Purchase Requests ─────────────────────────────────────────────

    @GetMapping("/purchase-requests")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Page<PurchaseRequestResponse>>> getApprovedRequests(
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PurchaseRequest> page = purchaseRequestRepository
                .findByStatusAndIsDeletedFalse(PurchaseRequestStatus.APPROVED, pageable);

        Page<PurchaseRequestResponse> response = page.map(this::mapRequest);
        return ResponseEntity.ok(ApiResponse.success("Approved purchase requests fetched successfully.", response));
    }

    @GetMapping("/purchase-requests/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<PurchaseRequestResponse>> getApprovedRequestById(@PathVariable Long id) {
        PurchaseRequest request = purchaseRequestRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Request", id));
        return ResponseEntity.ok(ApiResponse.success("Purchase request fetched successfully.", mapRequest(request)));
    }

    // ── Purchase Orders ────────────────────────────────────────────────────────

    @PostMapping("/purchase-orders")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> createPurchaseOrder(
            @Valid @RequestBody CreatePurchaseOrderRequest request) {

        // Prevent duplicate PO for the same request
        if (purchaseOrderRepository.existsByPurchaseRequestIdAndIsDeletedFalse(request.getPurchaseRequestId())) {
            throw new DuplicateResourceException("A Purchase Order already exists for this request.");
        }

        PurchaseRequest purchaseRequest = purchaseRequestRepository
                .findByIdAndIsDeletedFalse(request.getPurchaseRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Request", request.getPurchaseRequestId()));

        if (purchaseRequest.getStatus() != PurchaseRequestStatus.APPROVED) {
            throw new InvalidRequestException("Only APPROVED purchase requests can be converted to a Purchase Order.");
        }

        Vendor vendor = vendorRepository.findByIdAndIsDeletedFalse(request.getVendorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", request.getVendorId()));

        if (!Boolean.TRUE.equals(vendor.getIsActive())) {
            throw new InvalidRequestException("Selected vendor is not active.");
        }

        BigDecimal totalAmount = purchaseRequest.getTotalAmount() != null
                ? purchaseRequest.getTotalAmount()
                : BigDecimal.ZERO;

        PurchaseOrder po = PurchaseOrder.builder()
                .purchaseOrderNumber(generatePoNumber())
                .purchaseRequest(purchaseRequest)
                .vendor(vendor)
                .status(PurchaseOrderStatus.CREATED)
                .totalAmount(totalAmount)
                .expectedDeliveryDate(request.getExpectedDeliveryDate())
                .isDeleted(false)
                .build();

        PurchaseOrder saved = purchaseOrderRepository.save(po);

        // Copy items from purchase request to PO
        List<PurchaseRequestItem> requestItems = purchaseRequestItemRepository
                .findByPurchaseRequestIdAndIsDeletedFalse(purchaseRequest.getId());

        for (PurchaseRequestItem item : requestItems) {
            BigDecimal unitPrice = item.getEstimatedPrice() != null ? item.getEstimatedPrice() : BigDecimal.ZERO;
            BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
            PurchaseOrderItem poItem = PurchaseOrderItem.builder()
                    .purchaseOrder(saved)
                    .product(item.getProduct())
                    .quantity(item.getQuantity())
                    .unitPrice(unitPrice)
                    .totalPrice(total)
                    .isDeleted(false)
                    .build();
            purchaseOrderItemRepository.save(poItem);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Purchase Order created successfully.", mapOrder(saved), HttpStatus.CREATED));
    }

    @GetMapping("/purchase-orders")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Page<PurchaseOrderResponse>>> getAllPurchaseOrders(
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PurchaseOrderResponse> response = purchaseOrderRepository
                .findAllByIsDeletedFalse(pageable)
                .map(this::mapOrder);

        return ResponseEntity.ok(ApiResponse.success("Purchase orders fetched successfully.", response));
    }

    @GetMapping("/purchase-orders/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> getPurchaseOrderById(@PathVariable Long id) {
        PurchaseOrder po = purchaseOrderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order", id));
        return ResponseEntity.ok(ApiResponse.success("Purchase order fetched successfully.", mapOrder(po)));
    }

    @PatchMapping("/purchase-orders/{id}/assign-vendor")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> assignVendor(
            @PathVariable Long id,
            @Valid @RequestBody AssignVendorRequest request) {

        PurchaseOrder po = purchaseOrderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order", id));

        if (po.getStatus() != PurchaseOrderStatus.CREATED) {
            throw new InvalidRequestException("Vendor can only be changed before the order is sent.");
        }

        Vendor vendor = vendorRepository.findByIdAndIsDeletedFalse(request.getVendorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", request.getVendorId()));

        if (!Boolean.TRUE.equals(vendor.getIsActive())) {
            throw new InvalidRequestException("Selected vendor is not active.");
        }

        po.setVendor(vendor);
        PurchaseOrder saved = purchaseOrderRepository.save(po);
        return ResponseEntity.ok(ApiResponse.success("Vendor assigned successfully.", mapOrder(saved)));
    }

    @PatchMapping("/purchase-orders/{id}/send")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> sendPurchaseOrder(@PathVariable Long id) {
        PurchaseOrder po = purchaseOrderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order", id));

        if (po.getStatus() != PurchaseOrderStatus.CREATED) {
            throw new InvalidRequestException("Only CREATED purchase orders can be sent.");
        }

        po.setStatus(PurchaseOrderStatus.SENT);
        PurchaseOrder saved = purchaseOrderRepository.save(po);
        return ResponseEntity.ok(ApiResponse.success("Purchase Order sent to vendor.", mapOrder(saved)));
    }

    // ── Vendors ────────────────────────────────────────────────────────────────

    @GetMapping("/vendors")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Page<VendorResponse>>> getActiveVendors(
            @ParameterObject
            @PageableDefault(size = 50, sort = "vendorName") Pageable pageable) {

        Page<VendorResponse> response = vendorRepository
                .findByIsActiveAndIsDeletedFalse(true, pageable)
                .map(this::mapVendor);

        return ResponseEntity.ok(ApiResponse.success("Active vendors fetched successfully.", response));
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private String generatePoNumber() {
        long next = purchaseOrderRepository.countByIsDeletedFalse() + 1;
        return String.format("%s%d-%04d", Constants.PO_PREFIX, Year.now().getValue(), next);
    }

    private PurchaseRequestResponse mapRequest(PurchaseRequest r) {
        List<PurchaseRequestItem> items = purchaseRequestItemRepository
                .findByPurchaseRequestIdAndIsDeletedFalse(r.getId());
        PurchaseRequestItem primary = items.isEmpty() ? null : items.get(0);

        return PurchaseRequestResponse.builder()
                .id(r.getId())
                .requestNumber(r.getRequestNumber())
                .title(r.getTitle())
                .requesterId(r.getRequester() != null ? r.getRequester().getId() : null)
                .requesterName(r.getRequester() != null
                        ? r.getRequester().getFirstName() + " " + r.getRequester().getLastName() : null)
                .departmentId(r.getDepartment() != null ? r.getDepartment().getId() : null)
                .departmentName(r.getDepartment() != null ? r.getDepartment().getName() : null)
                .priority(r.getPriority())
                .status(r.getStatus())
                .approvalStatus(r.getStatus())
                .totalAmount(r.getTotalAmount())
                .approvalDate(r.getApprovalDate())
                .productId(primary != null && primary.getProduct() != null ? primary.getProduct().getId() : null)
                .productName(primary != null && primary.getProduct() != null ? primary.getProduct().getName() : null)
                .quantity(primary != null ? primary.getQuantity() : null)
                .unitPrice(primary != null ? primary.getEstimatedPrice() : null)
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }

    private PurchaseOrderResponse mapOrder(PurchaseOrder po) {
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
}

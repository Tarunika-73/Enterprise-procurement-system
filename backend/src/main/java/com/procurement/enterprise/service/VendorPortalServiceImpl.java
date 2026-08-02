package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.request.VendorUpdateDeliveryRequest;
import com.procurement.enterprise.dto.response.*;
import com.procurement.enterprise.entity.Delivery;
import com.procurement.enterprise.entity.PurchaseOrder;
import com.procurement.enterprise.entity.Vendor;
import com.procurement.enterprise.enums.DeliveryStatus;
import com.procurement.enterprise.enums.PurchaseOrderStatus;
import com.procurement.enterprise.exception.InvalidRequestException;
import com.procurement.enterprise.exception.ResourceNotFoundException;
import com.procurement.enterprise.repository.DeliveryRepository;
import com.procurement.enterprise.repository.PurchaseOrderRepository;
import com.procurement.enterprise.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VendorPortalServiceImpl implements VendorPortalService {

    private static final Logger log = LoggerFactory.getLogger(VendorPortalServiceImpl.class);

    private final VendorRepository vendorRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final DeliveryRepository deliveryRepository;

    /* ── helpers ─────────────────────────────────────────────────── */

    private Vendor resolveVendor(String vendorEmail) {
        return vendorRepository.findByEmailAndIsDeletedFalse(vendorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found for email: " + vendorEmail));
    }

    private PurchaseOrder resolveOwnedPO(Vendor vendor, Long purchaseOrderId) {
        PurchaseOrder po = purchaseOrderRepository.findByIdAndIsDeletedFalse(purchaseOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", purchaseOrderId));
        if (!po.getVendor().getId().equals(vendor.getId())) {
            throw new InvalidRequestException("Access denied to this purchase order");
        }
        return po;
    }

    private VendorPurchaseOrderResponse mapPO(PurchaseOrder po) {
        String deptName = po.getPurchaseRequest() != null && po.getPurchaseRequest().getDepartment() != null
                ? po.getPurchaseRequest().getDepartment().getName() : "—";
        String officerName = po.getPurchaseRequest() != null && po.getPurchaseRequest().getRequester() != null
                ? po.getPurchaseRequest().getRequester().getFirstName() + " " + po.getPurchaseRequest().getRequester().getLastName()
                : "—";
        String deliveryAddress = po.getPurchaseRequest() != null && po.getPurchaseRequest().getDepartment() != null
                ? po.getPurchaseRequest().getDepartment().getName() + " Department" : "—";

        List<PurchaseOrderItemResponse> items = po.getItems().stream()
                .filter(i -> !Boolean.TRUE.equals(i.getIsDeleted()))
                .map(i -> PurchaseOrderItemResponse.builder()
                        .id(i.getId())
                        .purchaseOrderId(po.getId())
                        .productId(i.getProduct() != null ? i.getProduct().getId() : null)
                        .productName(i.getProduct() != null ? i.getProduct().getName() : "—")
                        .productSku(i.getProduct() != null ? i.getProduct().getSku() : "—")
                        .quantity(i.getQuantity())
                        .unitPrice(i.getUnitPrice())
                        .totalPrice(i.getTotalPrice())
                        .createdAt(i.getCreatedAt())
                        .updatedAt(i.getUpdatedAt())
                        .build())
                .toList();

        return VendorPurchaseOrderResponse.builder()
                .id(po.getId())
                .purchaseOrderNumber(po.getPurchaseOrderNumber())
                .departmentName(deptName)
                .procurementOfficerName(officerName)
                .deliveryAddress(deliveryAddress)
                .expectedDeliveryDate(po.getExpectedDeliveryDate())
                .totalAmount(po.getTotalAmount())
                .status(po.getStatus())
                .createdAt(po.getCreatedAt())
                .items(items)
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

    /* ── dashboard ───────────────────────────────────────────────── */

    @Override
    @Transactional(readOnly = true)
    public VendorDashboardResponse getDashboard(String vendorEmail) {
        Vendor vendor = resolveVendor(vendorEmail);

        Page<PurchaseOrder> allPage = purchaseOrderRepository
                .findByVendorIdAndIsDeletedFalse(vendor.getId(), Pageable.unpaged());
        List<PurchaseOrder> all = allPage.getContent();

        long pending = all.stream().filter(p -> p.getStatus() == PurchaseOrderStatus.SENT
                || p.getStatus() == PurchaseOrderStatus.ACCEPTED).count();
        long delivered = all.stream().filter(p -> p.getStatus() == PurchaseOrderStatus.DELIVERED).count();
        BigDecimal totalValue = all.stream().map(PurchaseOrder::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Pageable recent = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<VendorPurchaseOrderResponse> recentOrders = purchaseOrderRepository
                .findByVendorIdAndIsDeletedFalse(vendor.getId(), recent)
                .getContent().stream().map(this::mapPO).toList();

        return VendorDashboardResponse.builder()
                .vendorId(vendor.getId())
                .vendorName(vendor.getVendorName())
                .totalOrders(all.size())
                .pendingDelivery(pending)
                .deliveredOrders(delivered)
                .totalOrderValue(totalValue)
                .recentOrders(recentOrders)
                .build();
    }

    /* ── purchase orders ─────────────────────────────────────────── */

    @Override
    @Transactional(readOnly = true)
    public Page<VendorPurchaseOrderResponse> getPurchaseOrders(String vendorEmail,
                                                               PurchaseOrderStatus status,
                                                               Pageable pageable) {
        Vendor vendor = resolveVendor(vendorEmail);
        if (status != null) {
            return purchaseOrderRepository
                    .findByVendorIdAndStatusAndIsDeletedFalse(vendor.getId(), status, pageable)
                    .map(this::mapPO);
        }
        return purchaseOrderRepository
                .findByVendorIdAndIsDeletedFalse(vendor.getId(), pageable)
                .map(this::mapPO);
    }

    @Override
    @Transactional(readOnly = true)
    public VendorPurchaseOrderResponse getPurchaseOrderDetail(String vendorEmail, Long purchaseOrderId) {
        Vendor vendor = resolveVendor(vendorEmail);
        return mapPO(resolveOwnedPO(vendor, purchaseOrderId));
    }

    /* ── accept / reject ─────────────────────────────────────────── */

    @Override
    @Transactional
    public VendorPurchaseOrderResponse acceptOrder(String vendorEmail, Long purchaseOrderId) {
        Vendor vendor = resolveVendor(vendorEmail);
        PurchaseOrder po = resolveOwnedPO(vendor, purchaseOrderId);

        if (po.getStatus() != PurchaseOrderStatus.SENT) {
            throw new InvalidRequestException("Only SENT orders can be accepted. Current status: " + po.getStatus());
        }
        po.setStatus(PurchaseOrderStatus.ACCEPTED);
        purchaseOrderRepository.save(po);
        log.info("Vendor {} accepted PO {}", vendor.getId(), po.getPurchaseOrderNumber());
        return mapPO(po);
    }

    @Override
    @Transactional
    public VendorPurchaseOrderResponse rejectOrder(String vendorEmail, Long purchaseOrderId, String remarks) {
        Vendor vendor = resolveVendor(vendorEmail);
        PurchaseOrder po = resolveOwnedPO(vendor, purchaseOrderId);

        if (po.getStatus() != PurchaseOrderStatus.SENT) {
            throw new InvalidRequestException("Only SENT orders can be rejected. Current status: " + po.getStatus());
        }
        if (remarks == null || remarks.isBlank()) {
            throw new InvalidRequestException("Remarks are required when rejecting an order");
        }
        po.setStatus(PurchaseOrderStatus.REJECTED);
        purchaseOrderRepository.save(po);
        log.info("Vendor {} rejected PO {} with remarks: {}", vendor.getId(), po.getPurchaseOrderNumber(), remarks);
        return mapPO(po);
    }

    /* ── delivery update ─────────────────────────────────────────── */

    @Override
    @Transactional
    public DeliveryResponse updateDelivery(String vendorEmail, VendorUpdateDeliveryRequest request) {
        Vendor vendor = resolveVendor(vendorEmail);
        PurchaseOrder po = resolveOwnedPO(vendor, request.getPurchaseOrderId());

        if (po.getStatus() == PurchaseOrderStatus.SENT || po.getStatus() == PurchaseOrderStatus.CREATED) {
            throw new InvalidRequestException("Order must be accepted before updating delivery");
        }

        // Validate dispatch number required for non-PREPARING statuses
        if (request.getDeliveryStatus() != DeliveryStatus.PENDING
                && (request.getDispatchNumber() == null || request.getDispatchNumber().isBlank())) {
            throw new InvalidRequestException("Dispatch number is required after shipment");
        }

        // Validate dates
        if (request.getDispatchDate() != null && po.getCreatedAt() != null
                && request.getDispatchDate().isBefore(po.getCreatedAt().toLocalDate())) {
            throw new InvalidRequestException("Dispatch date cannot be before order date");
        }
        if (request.getExpectedDeliveryDate() != null && request.getDispatchDate() != null
                && request.getExpectedDeliveryDate().isBefore(request.getDispatchDate())) {
            throw new InvalidRequestException("Expected delivery date cannot be before dispatch date");
        }

        // Upsert delivery record
        Delivery delivery;
        boolean exists = deliveryRepository.existsByPurchaseOrderIdAndIsDeletedFalse(po.getId());
        if (exists) {
            delivery = deliveryRepository
                    .findByPurchaseOrderIdAndIsDeletedFalse(po.getId(), Pageable.unpaged())
                    .getContent().get(0);
        } else {
            delivery = Delivery.builder()
                    .purchaseOrder(po)
                    .isDeleted(false)
                    .build();
        }

        delivery.setStatus(request.getDeliveryStatus());
        delivery.setTrackingNumber(request.getDispatchNumber());
        delivery.setDeliveryDate(request.getExpectedDeliveryDate());
        delivery.setCarrier(request.getRemarks());

        // Sync PO status
        if (request.getDeliveryStatus() == DeliveryStatus.DELIVERED) {
            po.setStatus(PurchaseOrderStatus.DELIVERED);
            purchaseOrderRepository.save(po);
        }

        Delivery saved = deliveryRepository.save(delivery);
        log.info("Vendor {} updated delivery for PO {}", vendor.getId(), po.getPurchaseOrderNumber());

        return DeliveryResponse.builder()
                .id(saved.getId())
                .purchaseOrderId(po.getId())
                .purchaseOrderNumber(po.getPurchaseOrderNumber())
                .deliveryNoteNumber(saved.getDeliveryNoteNumber())
                .deliveryDate(saved.getDeliveryDate())
                .status(saved.getStatus())
                .carrier(saved.getCarrier())
                .trackingNumber(saved.getTrackingNumber())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }

    /* ── profile ─────────────────────────────────────────────────── */

    @Override
    @Transactional(readOnly = true)
    public VendorResponse getProfile(String vendorEmail) {
        return mapVendor(resolveVendor(vendorEmail));
    }

    @Override
    @Transactional
    public VendorResponse updateProfile(String vendorEmail, String contactName, String phone, String address) {
        Vendor vendor = resolveVendor(vendorEmail);
        if (contactName != null) vendor.setContactName(contactName);
        if (phone != null) vendor.setPhone(phone);
        if (address != null) vendor.setAddress(address);
        return mapVendor(vendorRepository.save(vendor));
    }
}

package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.request.CreateSupplierPerformanceRequest;
import com.procurement.enterprise.dto.response.SupplierPerformanceResponse;
import com.procurement.enterprise.entity.PurchaseOrder;
import com.procurement.enterprise.entity.SupplierPerformance;
import com.procurement.enterprise.entity.Vendor;
import com.procurement.enterprise.exception.DuplicateResourceException;
import com.procurement.enterprise.exception.ResourceNotFoundException;
import com.procurement.enterprise.repository.PurchaseOrderRepository;
import com.procurement.enterprise.repository.SupplierPerformanceRepository;
import com.procurement.enterprise.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SupplierPerformanceServiceImpl implements SupplierPerformanceService {

    private static final Logger log = LoggerFactory.getLogger(SupplierPerformanceServiceImpl.class);

    private final SupplierPerformanceRepository supplierPerformanceRepository;
    private final VendorRepository vendorRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    @Override
    @Transactional
    public SupplierPerformanceResponse createSupplierPerformance(CreateSupplierPerformanceRequest request) {
        Vendor vendor = findActiveVendor(request.getVendorId());
        PurchaseOrder purchaseOrder = findActivePurchaseOrder(request.getPurchaseOrderId());

        if (supplierPerformanceRepository.existsByPurchaseOrderIdAndIsDeletedFalse(request.getPurchaseOrderId())) {
            throw new DuplicateResourceException(
                    "Supplier Performance",
                    "purchase order",
                    purchaseOrder.getPurchaseOrderNumber());
        }

        SupplierPerformance performance = SupplierPerformance.builder()
                .vendor(vendor)
                .purchaseOrder(purchaseOrder)
                .qualityRating(request.getQualityRating())
                .deliveryRating(request.getDeliveryRating())
                .pricingRating(request.getPricingRating())
                .comments(request.getComments())
                .isDeleted(false)
                .build();

        SupplierPerformance saved = supplierPerformanceRepository.save(performance);
        log.info("Created supplier performance with id: {}", saved.getId());
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public SupplierPerformanceResponse updateSupplierPerformance(Long id, CreateSupplierPerformanceRequest request) {
        SupplierPerformance performance = findActiveSupplierPerformance(id);
        Vendor vendor = findActiveVendor(request.getVendorId());
        PurchaseOrder purchaseOrder = findActivePurchaseOrder(request.getPurchaseOrderId());


        if (!performance.getPurchaseOrder().getId().equals(request.getPurchaseOrderId())
        && supplierPerformanceRepository.existsByPurchaseOrderIdAndIsDeletedFalse(
                request.getPurchaseOrderId())) {

    throw new DuplicateResourceException(
            "Supplier Performance",
            "purchase order",
            purchaseOrder.getPurchaseOrderNumber());
}
        
        performance.setVendor(vendor);
        performance.setPurchaseOrder(purchaseOrder);
        performance.setQualityRating(request.getQualityRating());
        performance.setDeliveryRating(request.getDeliveryRating());
        performance.setPricingRating(request.getPricingRating());
        performance.setComments(request.getComments());

        SupplierPerformance updated = supplierPerformanceRepository.save(performance);
        log.info("Updated supplier performance with id: {}", updated.getId());
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteSupplierPerformance(Long id) {
        SupplierPerformance performance = findActiveSupplierPerformance(id);
        performance.setIsDeleted(true);
        supplierPerformanceRepository.save(performance);
        log.info("Soft deleted supplier performance with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierPerformanceResponse getSupplierPerformanceById(Long id) {
        return mapToResponse(findActiveSupplierPerformance(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplierPerformanceResponse> getAllSupplierPerformance(Pageable pageable) {
        return supplierPerformanceRepository.findAllByIsDeletedFalse(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplierPerformanceResponse> getSupplierPerformanceByVendor(Long vendorId, Pageable pageable) {
        findActiveVendor(vendorId);
        return supplierPerformanceRepository.findByVendorIdAndIsDeletedFalse(vendorId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplierPerformanceResponse> getSupplierPerformanceByPurchaseOrder(Long purchaseOrderId, Pageable pageable) {
        findActivePurchaseOrder(purchaseOrderId);
        return supplierPerformanceRepository.findByPurchaseOrderIdAndIsDeletedFalse(purchaseOrderId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRating(Long vendorId) {
        findActiveVendor(vendorId);

        Double averageRating = supplierPerformanceRepository.findAverageRatingByVendorId(vendorId);

        return averageRating != null ? averageRating : 0.0;
    }

    private SupplierPerformance findActiveSupplierPerformance(Long id) {
        return supplierPerformanceRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier Performance", id));
    }

    private Vendor findActiveVendor(Long id) {
        return vendorRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", id));
    }

    private PurchaseOrder findActivePurchaseOrder(Long id) {
        return purchaseOrderRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order", id));
    }

    private SupplierPerformanceResponse mapToResponse(SupplierPerformance performance) {
        Vendor vendor = performance.getVendor();
        PurchaseOrder purchaseOrder = performance.getPurchaseOrder();

        return SupplierPerformanceResponse.builder()
                .id(performance.getId())
                .vendorId(vendor != null ? vendor.getId() : null)
                .vendorName(vendor != null ? vendor.getVendorName() : null)
                .purchaseOrderId(purchaseOrder != null ? purchaseOrder.getId() : null)
                .purchaseOrderNumber(purchaseOrder != null ? purchaseOrder.getPurchaseOrderNumber() : null)
                .qualityRating(performance.getQualityRating())
                .deliveryRating(performance.getDeliveryRating())
                .pricingRating(performance.getPricingRating())
                .comments(performance.getComments())
                .createdAt(performance.getCreatedAt())
                .updatedAt(performance.getUpdatedAt())
                .build();
    }
}

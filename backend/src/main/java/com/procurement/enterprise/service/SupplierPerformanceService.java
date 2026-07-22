package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.request.CreateSupplierPerformanceRequest;
import com.procurement.enterprise.dto.response.SupplierPerformanceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SupplierPerformanceService {

    /**
     * Create a new supplier performance record.
     */
    SupplierPerformanceResponse createSupplierPerformance(CreateSupplierPerformanceRequest request);

    /**
     * Update an existing supplier performance record.
     */
    SupplierPerformanceResponse updateSupplierPerformance(Long id, CreateSupplierPerformanceRequest request);

    /**
     * Soft delete a supplier performance record.
     */
    void deleteSupplierPerformance(Long id);

    /**
     * Get supplier performance by ID.
     */
    SupplierPerformanceResponse getSupplierPerformanceById(Long id);

    /**
     * Get all supplier performance records.
     */
    Page<SupplierPerformanceResponse> getAllSupplierPerformance(Pageable pageable);

    /**
     * Get supplier performance records by Vendor ID.
     */
    Page<SupplierPerformanceResponse> getSupplierPerformanceByVendor(Long vendorId, Pageable pageable);

    /**
     * Get supplier performance records by Purchase Order ID.
     */
    Page<SupplierPerformanceResponse> getSupplierPerformanceByPurchaseOrder(Long purchaseOrderId, Pageable pageable);

    /**
     * Get average supplier rating.
     */
    Double getAverageRating(Long vendorId);
}
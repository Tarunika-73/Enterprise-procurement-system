package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.SupplierPerformance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierPerformanceRepository extends JpaRepository<SupplierPerformance, Long> {
    Optional<SupplierPerformance> findByIdAndIsDeletedFalse(Long id);
    Page<SupplierPerformance> findAllByIsDeletedFalse(Pageable pageable);
    Page<SupplierPerformance> findByVendorIdAndIsDeletedFalse(Long vendorId, Pageable pageable);
    Page<SupplierPerformance> findByPurchaseOrderIdAndIsDeletedFalse(Long purchaseOrderId, Pageable pageable);

    @Query("SELECT AVG((sp.qualityRating + sp.deliveryRating + sp.pricingRating) / 3.0) " +
           "FROM SupplierPerformance sp WHERE sp.vendor.id = :vendorId AND sp.isDeleted = false")
    Double findAverageRatingByVendorId(Long vendorId);
}

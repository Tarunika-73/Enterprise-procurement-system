package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.SupplierPerformance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface SupplierPerformanceRepository extends JpaRepository<SupplierPerformance, Long> {

    Optional<SupplierPerformance> findByIdAndIsDeletedFalse(Long id);

    Page<SupplierPerformance> findAllByIsDeletedFalse(Pageable pageable);

    Page<SupplierPerformance> findByVendorIdAndIsDeletedFalse(Long vendorId, Pageable pageable);

    Page<SupplierPerformance> findByPurchaseOrderIdAndIsDeletedFalse(Long purchaseOrderId, Pageable pageable);

    boolean existsByPurchaseOrderIdAndIsDeletedFalse(Long purchaseOrderId);

    @Query("""
            SELECT AVG((sp.qualityRating + sp.deliveryRating + sp.pricingRating) / 3.0)
            FROM SupplierPerformance sp
            WHERE sp.vendor.id = :vendorId
            AND sp.isDeleted = false
            """)
    Double findAverageRatingByVendorId(@Param("vendorId") Long vendorId);

    @Query("SELECT sp.vendor.id, AVG((sp.qualityRating + sp.pricingRating) / 2.0), AVG(sp.deliveryRating) FROM SupplierPerformance sp WHERE sp.vendor.id IN :vendorIds AND sp.isDeleted = false GROUP BY sp.vendor.id")
    List<Object[]> findRecommendationRatingsByVendorIds(@Param("vendorIds") List<Long> vendorIds);
}

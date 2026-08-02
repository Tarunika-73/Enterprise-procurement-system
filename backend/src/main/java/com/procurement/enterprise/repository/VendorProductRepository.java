package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.VendorProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Repository for {@link VendorProduct} entity.
 */
@Repository
public interface VendorProductRepository extends JpaRepository<VendorProduct, Long> {

    Optional<VendorProduct> findByIdAndIsDeletedFalse(Long id);

    Optional<VendorProduct> findByVendorIdAndProductIdAndIsDeletedFalse(Long vendorId, Long productId);

    Page<VendorProduct> findAllByIsDeletedFalse(Pageable pageable);

    Page<VendorProduct> findByVendorIdAndIsDeletedFalse(Long vendorId, Pageable pageable);

    Page<VendorProduct> findByProductIdAndIsDeletedFalse(Long productId, Pageable pageable);

    Page<VendorProduct> findByIsActiveAndIsDeletedFalse(Boolean isActive, Pageable pageable);

    boolean existsByVendorIdAndProductIdAndIsDeletedFalse(Long vendorId, Long productId);

    @Query("""
            SELECT MIN(vp.price)
            FROM VendorProduct vp
            WHERE vp.product.id = :productId
              AND vp.isDeleted = false
              AND vp.isActive = true
            """)
    Optional<BigDecimal> findMinActivePriceByProductId(@Param("productId") Long productId);
}

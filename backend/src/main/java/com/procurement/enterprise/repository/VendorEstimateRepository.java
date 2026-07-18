package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.VendorEstimate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link VendorEstimate} entity.
 */
@Repository
public interface VendorEstimateRepository extends JpaRepository<VendorEstimate, Long> {

    Optional<VendorEstimate> findByIdAndIsDeletedFalse(Long id);

    Page<VendorEstimate> findAllByIsDeletedFalse(Pageable pageable);

    List<VendorEstimate> findByPurchaseRequestIdAndIsDeletedFalse(Long purchaseRequestId);

    Page<VendorEstimate> findByPurchaseRequestIdAndIsDeletedFalse(
            Long purchaseRequestId, Pageable pageable);

    Page<VendorEstimate> findByVendorIdAndIsDeletedFalse(Long vendorId, Pageable pageable);

    Optional<VendorEstimate> findByPurchaseRequestIdAndIsSelectedTrueAndIsDeletedFalse(
            Long purchaseRequestId);

    boolean existsByPurchaseRequestIdAndVendorIdAndIsDeletedFalse(
            Long purchaseRequestId, Long vendorId);
}

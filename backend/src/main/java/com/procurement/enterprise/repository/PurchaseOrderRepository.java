package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.PurchaseOrder;
import com.procurement.enterprise.enums.PurchaseOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository for {@link PurchaseOrder} entity.
 */
@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    Optional<PurchaseOrder> findByIdAndIsDeletedFalse(Long id);

    Optional<PurchaseOrder> findByPurchaseOrderNumberAndIsDeletedFalse(String purchaseOrderNumber);

    Optional<PurchaseOrder> findByPurchaseRequestIdAndIsDeletedFalse(Long purchaseRequestId);

    Page<PurchaseOrder> findAllByIsDeletedFalse(Pageable pageable);

    Page<PurchaseOrder> findByVendorIdAndIsDeletedFalse(Long vendorId, Pageable pageable);

    Page<PurchaseOrder> findByStatusAndIsDeletedFalse(PurchaseOrderStatus status, Pageable pageable);

    Page<PurchaseOrder> findByVendorIdAndStatusAndIsDeletedFalse(
            Long vendorId, PurchaseOrderStatus status, Pageable pageable);

    Page<PurchaseOrder> findByCreatedAtBetweenAndIsDeletedFalse(
            LocalDateTime from, LocalDateTime to, Pageable pageable);

    boolean existsByPurchaseOrderNumberAndIsDeletedFalse(String purchaseOrderNumber);

    boolean existsByPurchaseRequestIdAndIsDeletedFalse(Long purchaseRequestId);

    long countByIsDeletedFalse();
}

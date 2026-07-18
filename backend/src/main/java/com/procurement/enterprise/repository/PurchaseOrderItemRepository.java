package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.PurchaseOrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link PurchaseOrderItem} entity.
 */
@Repository
public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Long> {

    Optional<PurchaseOrderItem> findByIdAndIsDeletedFalse(Long id);

    List<PurchaseOrderItem> findByPurchaseOrderIdAndIsDeletedFalse(Long purchaseOrderId);

    Page<PurchaseOrderItem> findByPurchaseOrderIdAndIsDeletedFalse(
            Long purchaseOrderId, Pageable pageable);

    Page<PurchaseOrderItem> findByProductIdAndIsDeletedFalse(Long productId, Pageable pageable);
}

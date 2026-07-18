package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.PurchaseRequestItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link PurchaseRequestItem} entity.
 */
@Repository
public interface PurchaseRequestItemRepository extends JpaRepository<PurchaseRequestItem, Long> {

    Optional<PurchaseRequestItem> findByIdAndIsDeletedFalse(Long id);

    List<PurchaseRequestItem> findByPurchaseRequestIdAndIsDeletedFalse(Long purchaseRequestId);

    Page<PurchaseRequestItem> findByPurchaseRequestIdAndIsDeletedFalse(
            Long purchaseRequestId, Pageable pageable);

    Page<PurchaseRequestItem> findByProductIdAndIsDeletedFalse(Long productId, Pageable pageable);

    boolean existsByPurchaseRequestIdAndProductIdAndIsDeletedFalse(
            Long purchaseRequestId, Long productId);
}

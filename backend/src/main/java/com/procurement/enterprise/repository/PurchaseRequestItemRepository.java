package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.PurchaseRequestItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("SELECT DISTINCT pri.product.name FROM PurchaseRequestItem pri WHERE pri.purchaseRequest.requester.id = :userId AND pri.purchaseRequest.isDeleted = false AND pri.isDeleted = false ORDER BY pri.product.name")
    Page<String> findDistinctProductNamesByRequesterId(@Param("userId") Long userId, Pageable pageable);
}

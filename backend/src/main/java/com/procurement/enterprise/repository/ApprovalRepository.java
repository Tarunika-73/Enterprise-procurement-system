package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.Approval;
import com.procurement.enterprise.enums.ApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Approval} entity.
 */
@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long> {

    Optional<Approval> findByIdAndIsDeletedFalse(Long id);

    Page<Approval> findAllByIsDeletedFalse(Pageable pageable);

    List<Approval> findByPurchaseRequestIdAndIsDeletedFalse(Long purchaseRequestId);

    Page<Approval> findByPurchaseRequestIdAndIsDeletedFalse(Long purchaseRequestId, Pageable pageable);

    Page<Approval> findByApproverIdAndIsDeletedFalse(Long approverId, Pageable pageable);

    Page<Approval> findByStatusAndIsDeletedFalse(ApprovalStatus status, Pageable pageable);

    Page<Approval> findByApproverIdAndStatusAndIsDeletedFalse(
            Long approverId, ApprovalStatus status, Pageable pageable);

    Optional<Approval> findByPurchaseRequestIdAndLevelAndIsDeletedFalse(
            Long purchaseRequestId, Integer level);
}

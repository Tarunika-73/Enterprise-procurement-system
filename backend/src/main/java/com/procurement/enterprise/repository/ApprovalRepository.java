package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.Approval;
import com.procurement.enterprise.enums.ApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long> {


    // Find active approval by ID
    Optional<Approval> findByIdAndIsDeletedFalse(Long id);


    // Get all active approvals
    Page<Approval> findAllByIsDeletedFalse(Pageable pageable);


    // ===============================
    // Purchase Requisition based queries
    // ===============================

    List<Approval> findByPurchaseRequisitionIdAndIsDeletedFalse(
            Long purchaseRequisitionId
    );


    Page<Approval> findByPurchaseRequisitionIdAndIsDeletedFalse(
            Long purchaseRequisitionId,
            Pageable pageable
    );


    Optional<Approval> findByPurchaseRequisitionIdAndLevelAndIsDeletedFalse(
            Long purchaseRequisitionId,
            Integer level
    );


    /*
     * Used for approval workflow
     * Finds current pending approval for approver
     */
    Optional<Approval> findByPurchaseRequisitionIdAndApproverIdAndStatusAndIsDeletedFalse(
            Long purchaseRequisitionId,
            Long approverId,
            ApprovalStatus status
    );


    // ===============================
    // Approver based queries
    // ===============================

    Page<Approval> findByApproverIdAndIsDeletedFalse(
            Long approverId,
            Pageable pageable
    );


    List<Approval> findByApproverId(
            Long approverId
    );


    List<Approval> findByApproverIdAndStatus(
            Long approverId,
            ApprovalStatus status
    );


    Page<Approval> findByStatusAndIsDeletedFalse(
            ApprovalStatus status,
            Pageable pageable
    );


    Page<Approval> findByApproverIdAndStatusAndIsDeletedFalse(
            Long approverId,
            ApprovalStatus status,
            Pageable pageable
    );


    // ===============================
    // Compatibility methods
    // Existing services use PurchaseRequest naming
    // ===============================


    default Optional<Approval> findByPurchaseRequestIdAndLevelAndIsDeletedFalse(
            Long purchaseRequestId,
            Integer level
    ) {

        return findByPurchaseRequisitionIdAndLevelAndIsDeletedFalse(
                purchaseRequestId,
                level
        );
    }


    default List<Approval> findByPurchaseRequestIdAndIsDeletedFalse(
            Long purchaseRequestId
    ) {

        return findByPurchaseRequisitionIdAndIsDeletedFalse(
                purchaseRequestId
        );
    }


    default Page<Approval> findByPurchaseRequestIdAndIsDeletedFalse(
            Long purchaseRequestId,
            Pageable pageable
    ) {

        return findByPurchaseRequisitionIdAndIsDeletedFalse(
                purchaseRequestId,
                pageable
        );
    }

}
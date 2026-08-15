package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.ApprovalHistory;
import com.procurement.enterprise.enums.ApprovalActionTaken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApprovalHistoryRepository
        extends JpaRepository<ApprovalHistory, Long> {

    Optional<ApprovalHistory> findByIdAndIsDeletedFalse(Long id);

    List<ApprovalHistory>
    findByApprovalIdAndIsDeletedFalseOrderByCreatedAtDesc(Long approvalId);

    List<ApprovalHistory>
    findByPurchaseRequestIdAndIsDeletedFalseOrderByCreatedAtDesc(Long purchaseRequestId);

    Page<ApprovalHistory>
    findByApprovalIdAndIsDeletedFalseOrderByCreatedAtDesc(
            Long approvalId,
            Pageable pageable
    );

    List<ApprovalHistory>
    findByActionByIdAndIsDeletedFalseOrderByCreatedAtDesc(
            Long actionById
    );

    Page<ApprovalHistory>
    findByActionByIdAndIsDeletedFalseOrderByCreatedAtDesc(
            Long actionById,
            Pageable pageable
    );

    Page<ApprovalHistory>
    findByActionTakenAndIsDeletedFalseOrderByCreatedAtDesc(
            ApprovalActionTaken actionTaken,
            Pageable pageable
    );

    boolean existsByApprovalIdAndActionByIdAndIsDeletedFalse(
            Long approvalId,
            Long actionById
    );

    boolean existsByApprovalIdAndActionByIdAndActionTakenAndIsDeletedFalse(
            Long approvalId,
            Long actionById,
            ApprovalActionTaken actionTaken
    );
}

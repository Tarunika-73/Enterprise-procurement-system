package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.ApprovalHistory;
import com.procurement.enterprise.enums.ApprovalActionTaken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link ApprovalHistory} entity.
 */
@Repository
public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, Long> {

    Optional<ApprovalHistory> findByIdAndIsDeletedFalse(Long id);

    List<ApprovalHistory> findByApprovalIdAndIsDeletedFalse(Long approvalId);

    Page<ApprovalHistory> findByApprovalIdAndIsDeletedFalse(Long approvalId, Pageable pageable);

    Page<ApprovalHistory> findByActionByIdAndIsDeletedFalse(Long actionById, Pageable pageable);

    Page<ApprovalHistory> findByActionTakenAndIsDeletedFalse(
            ApprovalActionTaken actionTaken, Pageable pageable);
}

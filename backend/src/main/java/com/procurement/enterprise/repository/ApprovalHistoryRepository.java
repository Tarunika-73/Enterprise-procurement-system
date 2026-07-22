```java
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
 * Repository for ApprovalHistory entity.
 */
@Repository
public interface ApprovalHistoryRepository
        extends JpaRepository<ApprovalHistory, Long> {

    /*
     * Find one active approval-history record by history ID.
     */
    Optional<ApprovalHistory> findByIdAndIsDeletedFalse(Long id);

    /*
     * Get the complete history of an approval.
     * Latest action is returned first.
     */
    List<ApprovalHistory>
    findByApprovalIdAndIsDeletedFalseOrderByCreatedAtDesc(Long approvalId);

    /*
     * Get approval history with pagination.
     */
    Page<ApprovalHistory>
    findByApprovalIdAndIsDeletedFalseOrderByCreatedAtDesc(
            Long approvalId,
            Pageable pageable
    );

    /*
     * Get all actions performed by a particular user.
     * Latest action is returned first.
     */
    List<ApprovalHistory>
    findByActionByIdAndIsDeletedFalseOrderByCreatedAtDesc(Long actionById);

    /*
     * Get actions performed by a particular user with pagination.
     */
    Page<ApprovalHistory>
    findByActionByIdAndIsDeletedFalseOrderByCreatedAtDesc(
            Long actionById,
            Pageable pageable
    );

    /*
     * Get history records based on action type.
     * Example: APPROVED or REJECTED.
     */
    Page<ApprovalHistory>
    findByActionTakenAndIsDeletedFalseOrderByCreatedAtDesc(
            ApprovalActionTaken actionTaken,
            Pageable pageable
    );

    /*
     * Check whether a user has already taken any action
     * on the given approval.
     */
    boolean existsByApprovalIdAndActionByIdAndIsDeletedFalse(
            Long approvalId,
            Long actionById
    );

    /*
     * Check whether a user has already performed
     * a specific action on the given approval.
     */
    boolean existsByApprovalIdAndActionByIdAndActionTakenAndIsDeletedFalse(
            Long approvalId,
            Long actionById,
            ApprovalActionTaken actionTaken
    );
}
```

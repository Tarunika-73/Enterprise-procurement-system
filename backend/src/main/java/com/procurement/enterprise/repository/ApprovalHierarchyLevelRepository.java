package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.ApprovalHierarchyLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalHierarchyLevelRepository extends JpaRepository<ApprovalHierarchyLevel, Long> {

    List<ApprovalHierarchyLevel> findByApprovalHierarchyIdAndIsDeletedFalseOrderByLevelNumberAsc(
            Long approvalHierarchyId
    );

    Optional<ApprovalHierarchyLevel> findByApprovalHierarchyIdAndLevelNumberAndIsDeletedFalse(
            Long approvalHierarchyId,
            Integer levelNumber
    );

    int countByApprovalHierarchyIdAndIsDeletedFalse(Long approvalHierarchyId);
}

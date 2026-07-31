package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.ApprovalHierarchy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalHierarchyRepository extends JpaRepository<ApprovalHierarchy, Long> {

    Optional<ApprovalHierarchy> findByIdAndIsDeletedFalse(Long id);

    Page<ApprovalHierarchy> findAllByIsDeletedFalse(Pageable pageable);

    List<ApprovalHierarchy> findByDepartmentIdAndIsActiveTrueAndIsDeletedFalse(Long departmentId);

    List<ApprovalHierarchy> findByDepartmentIsNullAndIsActiveTrueAndIsDeletedFalse();

    /**
     * Finds every active, non-deleted hierarchy whose amount range
     * [minAmount, maxAmount] covers {@code amount}, for the given
     * department OR the global (department = null) rules, ordered by
     * priority (lowest first) with department-specific rules preferred
     * over global ones at the same priority.
     */
    @Query("""
            SELECT h FROM ApprovalHierarchy h
            WHERE h.isDeleted = false
              AND h.isActive = true
              AND (h.department.id = :departmentId OR h.department IS NULL)
              AND h.minAmount <= :amount
              AND (h.maxAmount IS NULL OR h.maxAmount >= :amount)
            ORDER BY h.priority ASC,
                     CASE WHEN h.department IS NULL THEN 1 ELSE 0 END ASC
            """)
    List<ApprovalHierarchy> findApplicableHierarchies(
            @Param("departmentId") Long departmentId,
            @Param("amount") BigDecimal amount
    );
}

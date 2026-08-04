package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.PurchaseRequest;
import com.procurement.enterprise.enums.PurchaseRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository for {@link PurchaseRequest} entity.
 */
@Repository
public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, Long> {

    Optional<PurchaseRequest> findByIdAndIsDeletedFalse(Long id);

    Optional<PurchaseRequest> findByRequestNumberAndIsDeletedFalse(String requestNumber);

    Page<PurchaseRequest> findAllByIsDeletedFalse(Pageable pageable);

    Page<PurchaseRequest> findByRequesterIdAndIsDeletedFalse(Long requesterId, Pageable pageable);

    Page<PurchaseRequest> findByDepartmentIdAndIsDeletedFalse(Long departmentId, Pageable pageable);

    Page<PurchaseRequest> findByStatusAndIsDeletedFalse(PurchaseRequestStatus status, Pageable pageable);

    Page<PurchaseRequest> findByRequesterIdAndStatusAndIsDeletedFalse(
            Long requesterId, PurchaseRequestStatus status, Pageable pageable);

    Page<PurchaseRequest> findByCreatedAtBetweenAndIsDeletedFalse(
            LocalDateTime from, LocalDateTime to, Pageable pageable);

    boolean existsByRequestNumberAndIsDeletedFalse(String requestNumber);

    long countByRequesterIdAndIsDeletedFalse(Long requesterId);

    long countByRequesterIdAndStatusAndIsDeletedFalse(Long requesterId, PurchaseRequestStatus status);

    Page<PurchaseRequest> findByRequesterIdAndIsDeletedFalseOrderByCreatedAtDesc(
            Long requesterId, Pageable pageable);

    Page<PurchaseRequest> findByManagerIdAndIsDeletedFalse(Long managerId, Pageable pageable);

    Page<PurchaseRequest> findByManagerIdAndIsDeletedFalseOrderByCreatedAtDesc(
            Long managerId, Pageable pageable);

    Page<PurchaseRequest> findByManagerIdAndStatusAndIsDeletedFalse(
            Long managerId, PurchaseRequestStatus status, Pageable pageable);

    long countByManagerIdAndIsDeletedFalse(Long managerId);

    long countByManagerIdAndStatusAndIsDeletedFalse(Long managerId, PurchaseRequestStatus status);

    /* ── reporting ───────────────────────────────────────────────── */

    long countByIsDeletedFalse();

    long countByStatusAndIsDeletedFalse(PurchaseRequestStatus status);

    long countByDepartmentIdAndIsDeletedFalse(Long departmentId);

    long countByDepartmentIdAndStatusAndIsDeletedFalse(Long departmentId, PurchaseRequestStatus status);

    Page<PurchaseRequest> findByDepartmentIdAndIsDeletedFalseOrderByCreatedAtDesc(
            Long departmentId, Pageable pageable);

    Page<PurchaseRequest> findAllByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);
}

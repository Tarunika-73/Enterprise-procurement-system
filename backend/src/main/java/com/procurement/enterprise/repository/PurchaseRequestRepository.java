package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.PurchaseRequest;
import com.procurement.enterprise.enums.PurchaseRequestStatus;
import com.procurement.enterprise.enums.RequestPriority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
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

    long countByManagerIdAndPriorityAndIsDeletedFalse(Long managerId, RequestPriority priority);

    /* ── reporting ───────────────────────────────────────────────── */

    long countByIsDeletedFalse();

    long countByStatusAndIsDeletedFalse(PurchaseRequestStatus status);

    long countByDepartmentIdAndIsDeletedFalse(Long departmentId);

    long countByDepartmentIdAndStatusAndIsDeletedFalse(Long departmentId, PurchaseRequestStatus status);

    Page<PurchaseRequest> findByDepartmentIdAndIsDeletedFalseOrderByCreatedAtDesc(
            Long departmentId, Pageable pageable);

    Page<PurchaseRequest> findAllByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT COALESCE(SUM(pr.totalAmount), 0) FROM PurchaseRequest pr WHERE pr.requester.id = :userId AND pr.isDeleted = false")
    BigDecimal sumTotalAmountByRequesterId(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(pr.totalAmount), 0) FROM PurchaseRequest pr WHERE pr.manager.id = :managerId AND pr.status = :status AND pr.isDeleted = false")
    BigDecimal sumTotalAmountByManagerIdAndStatus(@Param("managerId") Long managerId, @Param("status") PurchaseRequestStatus status);

    @Query("SELECT COALESCE(SUM(pr.totalAmount), 0) FROM PurchaseRequest pr WHERE pr.isDeleted = false")
    BigDecimal sumTotalAmountByIsDeletedFalse();

    @Query("SELECT pr.status, COUNT(pr) FROM PurchaseRequest pr WHERE pr.isDeleted = false GROUP BY pr.status")
    List<Object[]> countByStatusForOrganization();

    @Query("SELECT pr.status, COUNT(pr) FROM PurchaseRequest pr WHERE pr.requester.id = :userId AND pr.isDeleted = false GROUP BY pr.status")
    List<Object[]> countByStatusForRequester(@Param("userId") Long userId);

    @Query("SELECT pr.status, COUNT(pr) FROM PurchaseRequest pr WHERE pr.manager.id = :managerId AND pr.isDeleted = false GROUP BY pr.status")
    List<Object[]> countByStatusForManager(@Param("managerId") Long managerId);

    @Query("SELECT pr.status, COUNT(pr) FROM PurchaseRequest pr JOIN PurchaseOrder po ON po.purchaseRequest = pr WHERE po.vendor.id = :vendorId AND pr.isDeleted = false AND po.isDeleted = false GROUP BY pr.status")
    List<Object[]> countByStatusForVendor(@Param("vendorId") Long vendorId);

    @Query("SELECT YEAR(pr.createdAt), MONTH(pr.createdAt), COUNT(pr) FROM PurchaseRequest pr WHERE pr.createdAt >= :from AND pr.isDeleted = false GROUP BY YEAR(pr.createdAt), MONTH(pr.createdAt) ORDER BY YEAR(pr.createdAt), MONTH(pr.createdAt)")
    List<Object[]> monthlyTrendForOrganization(@Param("from") LocalDateTime from);

    @Query("SELECT YEAR(pr.createdAt), MONTH(pr.createdAt), COUNT(pr) FROM PurchaseRequest pr WHERE pr.requester.id = :userId AND pr.createdAt >= :from AND pr.isDeleted = false GROUP BY YEAR(pr.createdAt), MONTH(pr.createdAt) ORDER BY YEAR(pr.createdAt), MONTH(pr.createdAt)")
    List<Object[]> monthlyTrendForRequester(@Param("userId") Long userId, @Param("from") LocalDateTime from);

    @Query("SELECT YEAR(pr.createdAt), MONTH(pr.createdAt), COUNT(pr) FROM PurchaseRequest pr WHERE pr.manager.id = :managerId AND pr.createdAt >= :from AND pr.isDeleted = false GROUP BY YEAR(pr.createdAt), MONTH(pr.createdAt) ORDER BY YEAR(pr.createdAt), MONTH(pr.createdAt)")
    List<Object[]> monthlyTrendForManager(@Param("managerId") Long managerId, @Param("from") LocalDateTime from);

    @Query("SELECT YEAR(pr.createdAt), MONTH(pr.createdAt), COUNT(pr) FROM PurchaseRequest pr JOIN PurchaseOrder po ON po.purchaseRequest = pr WHERE po.vendor.id = :vendorId AND pr.createdAt >= :from AND pr.isDeleted = false AND po.isDeleted = false GROUP BY YEAR(pr.createdAt), MONTH(pr.createdAt) ORDER BY YEAR(pr.createdAt), MONTH(pr.createdAt)")
    List<Object[]> monthlyTrendForVendor(@Param("vendorId") Long vendorId, @Param("from") LocalDateTime from);

    @Query("SELECT FUNCTION('DATE', pr.createdAt), COUNT(pr) FROM PurchaseRequest pr WHERE pr.createdAt >= :from AND pr.isDeleted = false GROUP BY FUNCTION('DATE', pr.createdAt) ORDER BY FUNCTION('DATE', pr.createdAt)")
    List<Object[]> dailyTrendForOrganization(@Param("from") LocalDateTime from);

    @Query("SELECT FUNCTION('DATE', pr.createdAt), COUNT(pr) FROM PurchaseRequest pr WHERE pr.requester.id = :userId AND pr.createdAt >= :from AND pr.isDeleted = false GROUP BY FUNCTION('DATE', pr.createdAt) ORDER BY FUNCTION('DATE', pr.createdAt)")
    List<Object[]> dailyTrendForRequester(@Param("userId") Long userId, @Param("from") LocalDateTime from);

    @Query("SELECT FUNCTION('DATE', pr.createdAt), COUNT(pr) FROM PurchaseRequest pr WHERE pr.manager.id = :managerId AND pr.createdAt >= :from AND pr.isDeleted = false GROUP BY FUNCTION('DATE', pr.createdAt) ORDER BY FUNCTION('DATE', pr.createdAt)")
    List<Object[]> dailyTrendForManager(@Param("managerId") Long managerId, @Param("from") LocalDateTime from);

    @Query("SELECT FUNCTION('DATE', pr.createdAt), COUNT(pr) FROM PurchaseRequest pr JOIN PurchaseOrder po ON po.purchaseRequest = pr WHERE po.vendor.id = :vendorId AND pr.createdAt >= :from AND pr.isDeleted = false AND po.isDeleted = false GROUP BY FUNCTION('DATE', pr.createdAt) ORDER BY FUNCTION('DATE', pr.createdAt)")
    List<Object[]> dailyTrendForVendor(@Param("vendorId") Long vendorId, @Param("from") LocalDateTime from);
}

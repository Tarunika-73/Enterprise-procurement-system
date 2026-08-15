package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.PurchaseOrder;
import com.procurement.enterprise.enums.PurchaseOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link PurchaseOrder} entity.
 */
@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    Optional<PurchaseOrder> findByIdAndIsDeletedFalse(Long id);

    Optional<PurchaseOrder> findByPurchaseOrderNumberAndIsDeletedFalse(String purchaseOrderNumber);

    Optional<PurchaseOrder> findByPurchaseRequestIdAndIsDeletedFalse(Long purchaseRequestId);

    Page<PurchaseOrder> findAllByIsDeletedFalse(Pageable pageable);

    Page<PurchaseOrder> findByVendorIdAndIsDeletedFalse(Long vendorId, Pageable pageable);

    Page<PurchaseOrder> findByStatusAndIsDeletedFalse(PurchaseOrderStatus status, Pageable pageable);

    Page<PurchaseOrder> findByVendorIdAndStatusAndIsDeletedFalse(
            Long vendorId, PurchaseOrderStatus status, Pageable pageable);

    Page<PurchaseOrder> findByCreatedAtBetweenAndIsDeletedFalse(
            LocalDateTime from, LocalDateTime to, Pageable pageable);

    boolean existsByPurchaseOrderNumberAndIsDeletedFalse(String purchaseOrderNumber);

    boolean existsByPurchaseRequestIdAndIsDeletedFalse(Long purchaseRequestId);

    long countByIsDeletedFalse();

    long countByStatusAndIsDeletedFalse(PurchaseOrderStatus status);

    /* ── reporting ───────────────────────────────────────────────── */

    long countByPurchaseRequest_Requester_IdAndIsDeletedFalse(Long requesterId);

    long countByPurchaseRequest_Department_IdAndIsDeletedFalse(Long departmentId);

    List<PurchaseOrder> findTop5ByIsDeletedFalseOrderByCreatedAtDesc();

    List<PurchaseOrder> findTop5ByPurchaseRequest_Requester_IdAndIsDeletedFalseOrderByCreatedAtDesc(
            Long requesterId);

    List<PurchaseOrder> findTop5ByPurchaseRequest_Department_IdAndIsDeletedFalseOrderByCreatedAtDesc(
            Long departmentId);

    Page<PurchaseOrder> findByPurchaseRequest_Requester_IdAndIsDeletedFalseOrderByCreatedAtDesc(Long requesterId, Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT po.purchaseRequest.department.name, COALESCE(SUM(po.totalAmount), 0) FROM PurchaseOrder po WHERE po.isDeleted = false AND po.purchaseRequest.isDeleted = false GROUP BY po.purchaseRequest.department.name ORDER BY po.purchaseRequest.department.name")
    List<Object[]> spendingByDepartmentForOrganization();

    @org.springframework.data.jpa.repository.Query("SELECT po.purchaseRequest.department.name, COALESCE(SUM(po.totalAmount), 0) FROM PurchaseOrder po WHERE po.purchaseRequest.requester.id = :userId AND po.isDeleted = false AND po.purchaseRequest.isDeleted = false GROUP BY po.purchaseRequest.department.name ORDER BY po.purchaseRequest.department.name")
    List<Object[]> spendingByDepartmentForRequester(@org.springframework.data.repository.query.Param("userId") Long userId);

    @org.springframework.data.jpa.repository.Query("SELECT po.purchaseRequest.department.name, COALESCE(SUM(po.totalAmount), 0) FROM PurchaseOrder po WHERE po.purchaseRequest.manager.id = :managerId AND po.isDeleted = false AND po.purchaseRequest.isDeleted = false GROUP BY po.purchaseRequest.department.name ORDER BY po.purchaseRequest.department.name")
    List<Object[]> spendingByDepartmentForManager(@org.springframework.data.repository.query.Param("managerId") Long managerId);

    @org.springframework.data.jpa.repository.Query("SELECT po.purchaseRequest.department.name, COALESCE(SUM(po.totalAmount), 0) FROM PurchaseOrder po WHERE po.vendor.id = :vendorId AND po.isDeleted = false AND po.purchaseRequest.isDeleted = false GROUP BY po.purchaseRequest.department.name ORDER BY po.purchaseRequest.department.name")
    List<Object[]> spendingByDepartmentForVendor(@org.springframework.data.repository.query.Param("vendorId") Long vendorId);

    @org.springframework.data.jpa.repository.Query("SELECT po.vendor.id, COUNT(DISTINCT po), SUM(CASE WHEN po.status IN ('DELIVERED', 'CLOSED') THEN 1 ELSE 0 END) FROM PurchaseOrder po JOIN po.items item WHERE item.product.id = :productId AND po.vendor.id IN :vendorIds AND po.isDeleted = false AND item.isDeleted = false GROUP BY po.vendor.id")
    List<Object[]> recommendationHistoryByVendorAndProduct(@org.springframework.data.repository.query.Param("productId") Long productId, @org.springframework.data.repository.query.Param("vendorIds") List<Long> vendorIds);
}

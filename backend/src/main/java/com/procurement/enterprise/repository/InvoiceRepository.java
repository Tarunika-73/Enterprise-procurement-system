package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.Invoice;
import com.procurement.enterprise.enums.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.procurement.enterprise.enums.PurchaseOrderStatus;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByIdAndIsDeletedFalse(Long id);
    Optional<Invoice> findByInvoiceNumberAndIsDeletedFalse(String invoiceNumber);
    Optional<Invoice> findByReceiptIdAndIsDeletedFalse(Long receiptId);
    Page<Invoice> findAllByIsDeletedFalse(Pageable pageable);
    Page<Invoice> findByVendorIdAndIsDeletedFalse(Long vendorId, Pageable pageable);
    long countByVendorIdAndIsDeletedFalse(Long vendorId);
    Page<Invoice> findByStatusAndIsDeletedFalse(InvoiceStatus status, Pageable pageable);
    long countByStatusAndIsDeletedFalse(InvoiceStatus status);
    long countByIsDeletedFalse();
    boolean existsByInvoiceNumberAndIsDeletedFalse(String invoiceNumber);
    boolean existsByReceiptIdAndIsDeletedFalse(Long receiptId);

    @Query("SELECT i FROM Invoice i JOIN i.receipt r JOIN r.delivery d JOIN d.purchaseOrder po "
            + "WHERE i.isDeleted = false AND r.isDeleted = false AND d.isDeleted = false AND po.isDeleted = false "
            + "AND po.status = :purchaseOrderStatus AND i.status <> :paidStatus AND i.status <> :cancelledStatus "
            + "AND NOT EXISTS (SELECT p FROM Payment p WHERE p.invoice = i AND p.isDeleted = false)")
    Page<Invoice> findEligibleForPayment(@Param("purchaseOrderStatus") PurchaseOrderStatus purchaseOrderStatus,
                                         @Param("paidStatus") InvoiceStatus paidStatus,
                                         @Param("cancelledStatus") InvoiceStatus cancelledStatus,
                                         Pageable pageable);
}

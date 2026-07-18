package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.Invoice;
import com.procurement.enterprise.enums.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByIdAndIsDeletedFalse(Long id);
    Optional<Invoice> findByInvoiceNumberAndIsDeletedFalse(String invoiceNumber);
    Optional<Invoice> findByReceiptIdAndIsDeletedFalse(Long receiptId);
    Page<Invoice> findAllByIsDeletedFalse(Pageable pageable);
    Page<Invoice> findByVendorIdAndIsDeletedFalse(Long vendorId, Pageable pageable);
    Page<Invoice> findByStatusAndIsDeletedFalse(InvoiceStatus status, Pageable pageable);
    boolean existsByInvoiceNumberAndIsDeletedFalse(String invoiceNumber);
    boolean existsByReceiptIdAndIsDeletedFalse(Long receiptId);
}

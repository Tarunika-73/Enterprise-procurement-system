package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.Payment;
import com.procurement.enterprise.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByIdAndIsDeletedFalse(Long id);
    Optional<Payment> findByInvoiceIdAndIsDeletedFalse(Long invoiceId);
    Optional<Payment> findByPaymentReferenceAndIsDeletedFalse(String paymentReference);
    Page<Payment> findAllByIsDeletedFalse(Pageable pageable);
    Page<Payment> findByStatusAndIsDeletedFalse(PaymentStatus status, Pageable pageable);
    boolean existsByInvoiceIdAndIsDeletedFalse(Long invoiceId);
    boolean existsByPaymentReferenceAndIsDeletedFalse(String paymentReference);
}

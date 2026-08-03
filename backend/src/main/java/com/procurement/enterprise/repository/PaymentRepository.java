package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.Payment;
import com.procurement.enterprise.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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

    long countByStatusAndIsDeletedFalse(PaymentStatus status);

    long countByIsDeletedFalse();

    @Query("SELECT COALESCE(SUM(p.amountPaid), 0) FROM Payment p WHERE p.status = :status AND p.isDeleted = false")
    BigDecimal sumAmountByStatusAndIsDeletedFalse(PaymentStatus status);

    @Query("SELECT COALESCE(SUM(p.amountPaid), 0) FROM Payment p WHERE p.isDeleted = false")
    BigDecimal sumTotalAmountByIsDeletedFalse();
}

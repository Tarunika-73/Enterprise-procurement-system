package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.Receipt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    Optional<Receipt> findByIdAndIsDeletedFalse(Long id);
    Page<Receipt> findByDeliveryIdAndIsDeletedFalse(Long deliveryId, Pageable pageable);
    Page<Receipt> findAllByIsDeletedFalse(Pageable pageable);
    Page<Receipt> findByReceiverIdAndIsDeletedFalse(Long receiverId, Pageable pageable);
    boolean existsByDeliveryIdAndIsDeletedFalse(Long deliveryId);
}

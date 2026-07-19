package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.Delivery;
import com.procurement.enterprise.enums.DeliveryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByIdAndIsDeletedFalse(Long id);
    Page<Delivery> findByPurchaseOrderIdAndIsDeletedFalse(Long purchaseOrderId, Pageable pageable);
    Page<Delivery> findAllByIsDeletedFalse(Pageable pageable);
    Page<Delivery> findByStatusAndIsDeletedFalse(DeliveryStatus status, Pageable pageable);
    boolean existsByPurchaseOrderIdAndIsDeletedFalse(Long purchaseOrderId);
}

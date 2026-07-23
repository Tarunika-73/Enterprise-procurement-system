package com.procurement.enterprise.repository;
import com.procurement.enterprise.entity.PurchaseRequisition;
import com.procurement.enterprise.enums.RequisitionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PurchaseRequisitionRepository extends JpaRepository<PurchaseRequisition, Long> {

    Optional<PurchaseRequisition> findByIdAndIsDeletedFalse(Long id);

    Optional<PurchaseRequisition> findByRequestNumberAndIsDeletedFalse(String requestNumber);

    Page<PurchaseRequisition> findAllByIsDeletedFalse(Pageable pageable);

    Page<PurchaseRequisition> findByStatusAndIsDeletedFalse(
            RequisitionStatus status,
            Pageable pageable
    );

    Page<PurchaseRequisition> findByDescriptionContainingIgnoreCaseAndIsDeletedFalse(
            String description,
            Pageable pageable
    );

    boolean existsByRequestNumberAndIsDeletedFalse(String requestNumber);
}
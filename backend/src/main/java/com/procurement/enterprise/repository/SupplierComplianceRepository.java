package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.SupplierCompliance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface SupplierComplianceRepository extends JpaRepository<SupplierCompliance, Long> {
    Optional<SupplierCompliance> findByIdAndIsDeletedFalse(Long id);
    Page<SupplierCompliance> findAllByIsDeletedFalse(Pageable pageable);
    Page<SupplierCompliance> findByVendorIdAndIsDeletedFalse(Long vendorId, Pageable pageable);
    Page<SupplierCompliance> findByStatusAndIsDeletedFalse(String status, Pageable pageable);
    Page<SupplierCompliance> findByExpiryDateBeforeAndIsDeletedFalse(LocalDate date, Pageable pageable);
}

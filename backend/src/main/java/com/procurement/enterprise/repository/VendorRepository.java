package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for {@link Vendor} entity.
 */
@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {

    Optional<Vendor> findByIdAndIsDeletedFalse(Long id);

    Optional<Vendor> findByEmailAndIsDeletedFalse(String email);

    Optional<Vendor> findByGstNumberAndIsDeletedFalse(String gstNumber);

    Page<Vendor> findAllByIsDeletedFalse(Pageable pageable);

    Page<Vendor> findByIsActiveAndIsDeletedFalse(Boolean isActive, Pageable pageable);

    Page<Vendor> findByVendorNameContainingIgnoreCaseAndIsDeletedFalse(String vendorName, Pageable pageable);

    boolean existsByEmailAndIsDeletedFalse(String email);

    boolean existsByGstNumberAndIsDeletedFalse(String gstNumber);

    /* ── reporting ───────────────────────────────────────────────── */

    long countByIsDeletedFalse();

    long countByIsActiveTrueAndIsDeletedFalse();
}

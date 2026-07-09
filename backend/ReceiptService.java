package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.VendorEstimate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorEstimateRepository extends JpaRepository<VendorEstimate, Long> {
    // TODO: add custom query methods as needed
}

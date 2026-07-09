package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    // TODO: add custom query methods as needed
}

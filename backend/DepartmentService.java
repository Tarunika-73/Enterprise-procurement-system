package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.VendorProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorProductRepository extends JpaRepository<VendorProduct, Long> {
    // TODO: add custom query methods as needed
}

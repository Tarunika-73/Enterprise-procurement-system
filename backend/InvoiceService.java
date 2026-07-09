package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.PurchaseRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, Long> {
    // TODO: add custom query methods as needed
}

package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    // TODO: add custom query methods as needed
}

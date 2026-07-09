package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.PurchaseOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Long> {
    // TODO: add custom query methods as needed
}

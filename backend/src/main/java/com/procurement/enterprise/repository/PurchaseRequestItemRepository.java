package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.PurchaseRequestItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRequestItemRepository extends JpaRepository<PurchaseRequestItem, Long> {
    // TODO: add custom query methods as needed
}

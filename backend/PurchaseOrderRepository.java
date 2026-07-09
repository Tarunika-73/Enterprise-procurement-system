package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    // TODO: add custom query methods as needed
}

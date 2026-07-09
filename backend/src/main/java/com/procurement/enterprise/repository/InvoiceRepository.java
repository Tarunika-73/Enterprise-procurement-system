package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    // TODO: add custom query methods as needed
}

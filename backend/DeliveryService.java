package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // TODO: add custom query methods as needed
}

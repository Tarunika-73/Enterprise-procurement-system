package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    // TODO: add custom query methods as needed
}

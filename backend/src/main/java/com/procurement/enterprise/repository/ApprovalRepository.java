package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.Approval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long> {
    // TODO: add custom query methods as needed
}

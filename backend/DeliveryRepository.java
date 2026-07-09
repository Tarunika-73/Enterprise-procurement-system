package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    // TODO: add custom query methods as needed
}

package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Department} entity.
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByIdAndIsDeletedFalse(Long id);

    Optional<Department> findByNameAndIsDeletedFalse(String name);

    Optional<Department> findByCodeAndIsDeletedFalse(String code);

    List<Department> findAllByIsDeletedFalse();

    Page<Department> findAllByIsDeletedFalse(Pageable pageable);

    Page<Department> findByNameContainingIgnoreCaseAndIsDeletedFalse(String name, Pageable pageable);

    boolean existsByNameAndIsDeletedFalse(String name);

    boolean existsByCodeAndIsDeletedFalse(String code);

    List<Department> findByManagerIdAndIsDeletedFalse(Long managerId);
}

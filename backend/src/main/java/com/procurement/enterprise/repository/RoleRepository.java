package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Role} entity.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByIdAndIsDeletedFalse(Long id);

    Optional<Role> findByNameAndIsDeletedFalse(String name);

    List<Role> findAllByIsDeletedFalse();

    Page<Role> findAllByIsDeletedFalse(Pageable pageable);

    boolean existsByNameAndIsDeletedFalse(String name);

    Page<Role> findByNameContainingIgnoreCaseAndIsDeletedFalse(String name, Pageable pageable);
}

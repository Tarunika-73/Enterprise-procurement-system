package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for {@link User} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByIdAndIsDeletedFalse(Long id);

    Optional<User> findByEmailAndIsDeletedFalse(String email);

    Optional<User> findByEmployeeIdAndIsDeletedFalse(String employeeId);

    Page<User> findAllByIsDeletedFalse(Pageable pageable);

    Page<User> findByDepartmentIdAndIsDeletedFalse(Long departmentId, Pageable pageable);

    Page<User> findByRoleIdAndIsDeletedFalse(Long roleId, Pageable pageable);

    Page<User> findByIsActiveAndIsDeletedFalse(Boolean isActive, Pageable pageable);

    Page<User> findByFirstNameContainingIgnoreCaseAndIsDeletedFalse(String firstName, Pageable pageable);

    Page<User> findByLastNameContainingIgnoreCaseAndIsDeletedFalse(String lastName, Pageable pageable);

    boolean existsByEmailAndIsDeletedFalse(String email);

    boolean existsByEmployeeIdAndIsDeletedFalse(String employeeId);
}

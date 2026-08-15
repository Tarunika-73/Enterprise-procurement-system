package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link User} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByIdAndIsDeletedFalse(Long id);

    @EntityGraph(attributePaths = {"role", "department"})
    Optional<User> findByEmailAndIsDeletedFalse(String email);

    Optional<User> findByEmployeeIdAndIsDeletedFalse(String employeeId);

    Page<User> findAllByIsDeletedFalse(Pageable pageable);

    Page<User> findByDepartmentIdAndIsDeletedFalse(Long departmentId, Pageable pageable);

    boolean existsByDepartmentIdAndIsDeletedFalse(Long departmentId);

    Page<User> findByRoleIdAndIsDeletedFalse(Long roleId, Pageable pageable);

    Page<User> findByIsActiveAndIsDeletedFalse(Boolean isActive, Pageable pageable);

    Page<User> findByFirstNameContainingIgnoreCaseAndIsDeletedFalse(String firstName, Pageable pageable);

    Page<User> findByLastNameContainingIgnoreCaseAndIsDeletedFalse(String lastName, Pageable pageable);

    boolean existsByEmailAndIsDeletedFalse(String email);

    boolean existsByEmployeeIdAndIsDeletedFalse(String employeeId);

    @EntityGraph(attributePaths = {"role", "department"})
    @Query("""
            SELECT u FROM User u
            WHERE u.isDeleted = false
              AND u.isActive = true
              AND u.department.id = :departmentId
              AND LOWER(u.role.name) IN ('department manager', 'manager')
            """)
    List<User> findActiveManagersByDepartmentId(@Param("departmentId") Long departmentId);

    @EntityGraph(attributePaths = {"role", "department"})
    @Query("""
            SELECT u FROM User u
            WHERE u.isDeleted = false
              AND u.isActive = true
              AND LOWER(u.role.name) = 'procurement officer'
            """)
    List<User> findActiveProcurementOfficers();

    @EntityGraph(attributePaths = {"role", "department"})
    @Query("SELECT u FROM User u WHERE u.isDeleted = false AND u.isActive = true AND LOWER(u.role.name) = 'finance officer'")
    List<User> findActiveFinanceOfficers();

    long countByIsDeletedFalse();
}

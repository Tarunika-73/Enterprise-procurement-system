package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndIsDeletedFalse(Long id);

    Optional<Product> findBySkuAndIsDeletedFalse(String sku);

    Page<Product> findAllByIsDeletedFalse(Pageable pageable);

    Page<Product> findByCategoryIdAndIsDeletedFalse(Long categoryId, Pageable pageable);

    Page<Product> findByIsActiveAndIsDeletedFalse(Boolean isActive, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseAndIsDeletedFalse(String name, Pageable pageable);

    Page<Product> findBySkuContainingIgnoreCaseAndIsDeletedFalse(String sku, Pageable pageable);

    boolean existsBySkuAndIsDeletedFalse(String sku);

    @EntityGraph(attributePaths = {"category", "department"})
    @Query("""
            SELECT p FROM Product p
            WHERE p.isDeleted = false
              AND (:categoryId IS NULL OR p.category.id = :categoryId)
              AND (:departmentId IS NULL OR p.department.id = :departmentId)
              AND (
                    :keyword IS NULL
                    OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  )
            """)
    Page<Product> searchCatalog(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("departmentId") Long departmentId,
            Pageable pageable);

    @EntityGraph(attributePaths = {"category", "department"})
    @Query("""
            SELECT p FROM Product p
            WHERE p.isDeleted = false
              AND (
                    :keyword IS NULL
                    OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  )
              AND (:categoryId IS NULL OR p.category.id = :categoryId)
            ORDER BY CASE WHEN p.department.id = :employeeDepartmentId THEN 0 ELSE 1 END, p.name ASC
            """)
    List<Product> findCatalogForEmployee(
            @Param("employeeDepartmentId") Long employeeDepartmentId,
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId);

    List<Product> findByDepartmentIsNullAndIsDeletedFalse();
}

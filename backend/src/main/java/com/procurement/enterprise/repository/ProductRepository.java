package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for {@link Product} entity.
 */
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
}

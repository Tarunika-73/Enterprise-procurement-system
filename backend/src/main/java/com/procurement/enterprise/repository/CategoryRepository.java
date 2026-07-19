package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Category} entity.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByIdAndIsDeletedFalse(Long id);

    Optional<Category> findByNameAndIsDeletedFalse(String name);

    List<Category> findAllByIsDeletedFalse();

    Page<Category> findAllByIsDeletedFalse(Pageable pageable);

    Page<Category> findByNameContainingIgnoreCaseAndIsDeletedFalse(String name, Pageable pageable);

    boolean existsByNameAndIsDeletedFalse(String name);
}

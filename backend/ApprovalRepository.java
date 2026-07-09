package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // TODO: add custom query methods as needed
}

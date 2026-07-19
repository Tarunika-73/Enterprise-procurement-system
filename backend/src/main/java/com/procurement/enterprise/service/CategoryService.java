package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.request.CreateCategoryRequest;
import com.procurement.enterprise.dto.request.UpdateCategoryRequest;
import com.procurement.enterprise.dto.response.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Service interface for Category management. */
public interface CategoryService {

    CategoryResponse create(CreateCategoryRequest request);

    CategoryResponse update(Long id, UpdateCategoryRequest request);

    void delete(Long id);

    CategoryResponse getById(Long id);

    Page<CategoryResponse> getAll(Pageable pageable);

    Page<CategoryResponse> search(String name, Pageable pageable);
}

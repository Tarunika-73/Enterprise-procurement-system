package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.request.CreateProductRequest;
import com.procurement.enterprise.dto.request.UpdateProductRequest;
import com.procurement.enterprise.dto.response.EmployeeProductCatalogResponse;
import com.procurement.enterprise.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    ProductResponse create(CreateProductRequest request);

    ProductResponse update(Long id, UpdateProductRequest request);

    ProductResponse getById(Long id);

    Page<ProductResponse> getAll(Pageable pageable);

    Page<ProductResponse> search(String keyword, Long categoryId, Long departmentId, Pageable pageable);

    EmployeeProductCatalogResponse getCatalogForEmployee(String keyword, Long categoryId);

    void delete(Long id);
}

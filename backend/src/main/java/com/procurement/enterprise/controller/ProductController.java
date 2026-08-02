package com.procurement.enterprise.controller;

import com.procurement.enterprise.dto.request.CreateProductRequest;
import com.procurement.enterprise.dto.request.UpdateProductRequest;
import com.procurement.enterprise.dto.response.EmployeeProductCatalogResponse;
import com.procurement.enterprise.dto.response.ProductResponse;
import com.procurement.enterprise.service.ProductService;
import com.procurement.enterprise.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/v1/products", "/products"})
@RequiredArgsConstructor
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            @Valid @RequestBody CreateProductRequest request) {

        log.info("Creating product {}", request.getSku());
        ProductResponse response = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully", response, HttpStatus.CREATED));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long departmentId,
            @ParameterObject
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<ProductResponse> response = productService.search(search, categoryId, departmentId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Products fetched successfully", response));
    }

    @GetMapping("/employee-catalog")
    public ResponseEntity<ApiResponse<EmployeeProductCatalogResponse>> getEmployeeCatalog(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId) {

        EmployeeProductCatalogResponse response = productService.getCatalogForEmployee(search, categoryId);
        return ResponseEntity.ok(ApiResponse.success("Employee product catalog fetched successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Product fetched successfully", productService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success("Product updated successfully", productService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", null));
    }
}

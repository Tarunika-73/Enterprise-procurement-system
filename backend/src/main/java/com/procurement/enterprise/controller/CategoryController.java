package com.procurement.enterprise.controller;

import com.procurement.enterprise.dto.request.CreateCategoryRequest;
import com.procurement.enterprise.dto.request.UpdateCategoryRequest;
import com.procurement.enterprise.dto.response.CategoryResponse;
import com.procurement.enterprise.service.CategoryService;
import com.procurement.enterprise.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Procurement Category Management.
 */
@RestController
@RequestMapping({"/v1/categories", "/categories"})
@RequiredArgsConstructor
public class CategoryController {

    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CreateCategoryRequest request) {

        log.info("Creating category: {}", request.getName());

        CategoryResponse response = categoryService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Category created successfully",
                        response,
                        HttpStatus.CREATED));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CategoryResponse>>> getAllCategories(
            @ParameterObject Pageable pageable) {

        log.info("Fetching all categories");

        Page<CategoryResponse> response = categoryService.getAll(pageable);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Categories fetched successfully",
                        response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(
            @PathVariable Long id) {

        log.info("Fetching category {}", id);

        CategoryResponse response = categoryService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Category fetched successfully",
                        response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryRequest request) {

        log.info("Updating category {}", id);

        CategoryResponse response = categoryService.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Category updated successfully",
                        response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @PathVariable Long id) {

        log.info("Deleting category {}", id);

        categoryService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Category deleted successfully",
                        null));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<CategoryResponse>>> searchCategories(
            @RequestParam String name,
            @ParameterObject Pageable pageable) {

        log.info("Searching category {}", name);

        Page<CategoryResponse> response =
                categoryService.search(name, pageable);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Categories fetched successfully",
                        response));
    }
}
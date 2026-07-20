package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.request.CreateCategoryRequest;
import com.procurement.enterprise.dto.request.UpdateCategoryRequest;
import com.procurement.enterprise.dto.response.CategoryResponse;
import com.procurement.enterprise.entity.Category;
import com.procurement.enterprise.exception.DuplicateResourceException;
import com.procurement.enterprise.exception.InvalidRequestException;
import com.procurement.enterprise.exception.ResourceNotFoundException;
import com.procurement.enterprise.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryResponse create(CreateCategoryRequest request) {

        validateCategoryName(request.getName());

        String normalizedName = request.getName().trim();

        if (categoryRepository.existsByNameAndIsDeletedFalse(normalizedName)) {
            throw new DuplicateResourceException(
                    "Category",
                    "name",
                    normalizedName
            );
        }

        Category category = Category.builder()
                .name(normalizedName)
                .description(request.getDescription())
                .isDeleted(false)
                .build();

        Category saved = categoryRepository.save(category);

        log.info("Created category with id {}", saved.getId());

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public CategoryResponse update(Long id, UpdateCategoryRequest request) {

        Category category = findCategory(id);

        if (request.getName() != null) {

            validateCategoryName(request.getName());

            String normalizedName = request.getName().trim();

            if (!Objects.equals(category.getName(), normalizedName)
                    && categoryRepository.existsByNameAndIsDeletedFalse(normalizedName)) {

                throw new DuplicateResourceException(
                        "Category",
                        "name",
                        normalizedName
                );
            }

            category.setName(normalizedName);
        }

        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }

        Category updated = categoryRepository.save(category);

        log.info("Updated category {}", updated.getId());

        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {

        Category category = findCategory(id);

        category.setIsDeleted(true);

        categoryRepository.save(category);

        log.info("Deleted category {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {

        return mapToResponse(findCategory(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> getAll(Pageable pageable) {

        return categoryRepository
                .findAllByIsDeletedFalse(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> search(String name, Pageable pageable) {

        return categoryRepository
                .findByNameContainingIgnoreCaseAndIsDeletedFalse(name, pageable)
                .map(this::mapToResponse);
    }

    private Category findCategory(Long id) {

        return categoryRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category", id));
    }

    private void validateCategoryName(String name) {

        if (name == null || name.trim().isEmpty()) {
            throw new InvalidRequestException(
                    "Category name cannot be blank"
            );
        }
    }

    private CategoryResponse mapToResponse(Category category) {

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
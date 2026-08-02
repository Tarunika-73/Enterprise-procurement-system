package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.request.CreateProductRequest;
import com.procurement.enterprise.dto.request.UpdateProductRequest;
import com.procurement.enterprise.dto.response.EmployeeProductCatalogResponse;
import com.procurement.enterprise.dto.response.ProductResponse;
import com.procurement.enterprise.entity.Category;
import com.procurement.enterprise.entity.Department;
import com.procurement.enterprise.entity.Product;
import com.procurement.enterprise.entity.User;
import com.procurement.enterprise.exception.DuplicateResourceException;
import com.procurement.enterprise.exception.ResourceNotFoundException;
import com.procurement.enterprise.exception.UnauthorizedException;
import com.procurement.enterprise.repository.CategoryRepository;
import com.procurement.enterprise.repository.DepartmentRepository;
import com.procurement.enterprise.repository.ProductRepository;
import com.procurement.enterprise.repository.UserRepository;
import com.procurement.enterprise.repository.VendorProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final DepartmentRepository departmentRepository;
    private final VendorProductRepository vendorProductRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        String sku = request.getSku().trim().toUpperCase();
        if (productRepository.existsBySkuAndIsDeletedFalse(sku)) {
            throw new DuplicateResourceException("Product", "sku", sku);
        }

        Category category = categoryRepository.findByIdAndIsDeletedFalse(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));
        Department department = departmentRepository.findByIdAndIsDeletedFalse(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", request.getDepartmentId()));

        Product product = Product.builder()
                .sku(sku)
                .name(request.getName().trim())
                .description(request.getDescription())
                .category(category)
                .department(department)
                .availableQuantity(100)
                .isActive(true)
                .isDeleted(false)
                .build();

        Product saved = productRepository.save(product);
        log.info("Created product {} for department {}", saved.getId(), department.getId());
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, UpdateProductRequest request) {
        Product product = findActive(id);

        if (request.getName() != null) {
            product.setName(request.getName().trim());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findByIdAndIsDeletedFalse(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));
            product.setCategory(category);
        }
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findByIdAndIsDeletedFalse(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", request.getDepartmentId()));
            product.setDepartment(department);
        }
        if (request.getIsActive() != null) {
            product.setIsActive(request.getIsActive());
        }

        return mapToResponse(productRepository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        return mapToResponse(findActive(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAll(Pageable pageable) {
        return productRepository.findAllByIsDeletedFalse(pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> search(String keyword, Long categoryId, Long departmentId, Pageable pageable) {
        String normalizedKeyword = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        return productRepository.searchCatalog(normalizedKeyword, categoryId, departmentId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeProductCatalogResponse getCatalogForEmployee(String keyword, Long categoryId) {
        User employee = getCurrentUser();
        if (employee.getDepartment() == null) {
            throw new UnauthorizedException("Employee has no department assigned.");
        }

        Long departmentId = employee.getDepartment().getId();
        String normalizedKeyword = (keyword == null || keyword.isBlank()) ? null : keyword.trim();

        List<Product> products = productRepository.findCatalogForEmployee(
                departmentId, normalizedKeyword, categoryId);

        List<ProductResponse> departmentProducts = new ArrayList<>();
        List<ProductResponse> otherProducts = new ArrayList<>();

        for (Product product : products) {
            ProductResponse response = mapToResponse(product);
            if (product.getDepartment() != null
                    && Objects.equals(product.getDepartment().getId(), departmentId)) {
                departmentProducts.add(response);
            } else {
                otherProducts.add(response);
            }
        }

        return EmployeeProductCatalogResponse.builder()
                .departmentProducts(departmentProducts)
                .otherDepartmentProducts(otherProducts)
                .build();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Product product = findActive(id);
        product.setIsDeleted(true);
        product.setIsActive(false);
        productRepository.save(product);
    }

    private Product findActive(Long id) {
        return productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Authentication required.");
        }
        return userRepository.findByEmailAndIsDeletedFalse(authentication.getName())
                .orElseThrow(() -> new UnauthorizedException("Authenticated user not found."));
    }

    private ProductResponse mapToResponse(Product product) {
        BigDecimal price = vendorProductRepository
                .findMinActivePriceByProductId(product.getId())
                .orElse(BigDecimal.ZERO);

        int quantity = product.getAvailableQuantity() != null ? product.getAvailableQuantity() : 100;
        boolean active = Boolean.TRUE.equals(product.getIsActive());
        String status;
        if (!active) {
            status = "INACTIVE";
        } else if (quantity <= 0) {
            status = "OUT_OF_STOCK";
        } else {
            status = "ACTIVE";
        }

        return ProductResponse.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .departmentId(product.getDepartment() != null ? product.getDepartment().getId() : null)
                .departmentName(product.getDepartment() != null ? product.getDepartment().getName() : null)
                .price(price)
                .availableQuantity(quantity)
                .status(status)
                .isActive(active)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}

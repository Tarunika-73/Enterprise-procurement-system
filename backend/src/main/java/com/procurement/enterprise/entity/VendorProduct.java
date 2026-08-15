package com.procurement.enterprise.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA entity for the {@code vendor_products} table.
 * Represents a product offered by a specific vendor at a specific price.
 * The combination of vendor and product must be unique.
 */
@Entity
@Table(
    name = "vendor_products",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_vendor_product",
        columnNames = {"vendor_id", "product_id"}
    )
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * FK: {@code vendor_products.vendor_id → vendors.id}
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    /**
     * FK: {@code vendor_products.product_id → products.id}
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "lead_time_days")
    private Integer leadTimeDays;

    /** Stock available for procurement. Used by the vendor recommendation engine. */
    @Column(name = "available_quantity")
    @Builder.Default
    private Integer availableQuantity = 0;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

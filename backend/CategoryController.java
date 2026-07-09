package com.procurement.enterprise.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * TODO (Member 3 - Database): define real columns, relationships,
 * and constraints for Receipt. This is a placeholder so the package
 * structure matches the project plan.
 */
@Entity
@Table(name = "receipts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: add fields specific to Receipt
}

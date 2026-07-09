package com.procurement.enterprise.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * TODO (Member 3 - Database): define real columns, relationships,
 * and constraints for Department. This is a placeholder so the package
 * structure matches the project plan.
 */
@Entity
@Table(name = "departments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: add fields specific to Department
}

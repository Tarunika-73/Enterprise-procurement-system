package com.procurement.enterprise.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * TODO (Member 3 - Database): define real columns, relationships,
 * and constraints for User. This is a placeholder so the package
 * structure matches the project plan.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: add fields specific to User
}

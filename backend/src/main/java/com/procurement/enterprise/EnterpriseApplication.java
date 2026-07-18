package com.procurement.enterprise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Entry point for the Enterprise Procurement Management System.
 * Enables JPA auditing for automatic population of createdAt and updatedAt fields.
 */
@SpringBootApplication
@EnableJpaAuditing
public class EnterpriseApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnterpriseApplication.class, args);
    }
}

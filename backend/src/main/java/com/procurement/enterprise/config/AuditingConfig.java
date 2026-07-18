package com.procurement.enterprise.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables JPA auditing so that {@code @CreatedDate} and {@code @LastModifiedDate}
 * annotations on entities are automatically populated by Spring Data.
 */
@Configuration
@EnableJpaAuditing
public class AuditingConfig {
}

package com.procurement.enterprise.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3 / Swagger UI configuration.
 * Registers a JWT Bearer security scheme so all endpoints can be tested
 * with an Authorization header directly from the Swagger UI.
 */
@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Enterprise Procurement System API")
                        .description("Enterprise Procurement Backend REST APIs — " +
                                "Manages the full procurement lifecycle from purchase requests " +
                                "through approvals, purchase orders, delivery, invoicing, and payment.")
                        .version("v1")
                        .contact(new Contact()
                                .name("Procurement Engineering Team")
                                .email("procurement-dev@enterprise.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://enterprise.com")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Provide the JWT access token obtained from /api/v1/auth/login")));
    }
}

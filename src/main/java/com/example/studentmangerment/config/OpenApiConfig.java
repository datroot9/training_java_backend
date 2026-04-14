package com.example.studentmangerment.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger metadata and global JWT security scheme.
 *
 * <p>Declares a bearer scheme named {@code bearerAuth} as the default
 * security requirement for documented endpoints.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Student Management API",
                description = "Documentation for Student Management REST API",
                version = "v1"
        ),
        // This globally applies the security requirement to all endpoints.
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth", // The name used in @SecurityRequirement
        description = "JWT auth description",
        scheme = "bearer", // Indicates HTTP Bearer authentication
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}

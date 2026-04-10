package com.example.studentmangerment.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
/**
 * OpenAPI/Swagger metadata and global security scheme declaration.
 *
 * <p>Declares a JWT bearer scheme named {@code bearerAuth} and applies it as the
 * default requirement for all documented endpoints.
 */
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

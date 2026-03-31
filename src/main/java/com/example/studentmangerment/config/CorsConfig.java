package com.example.studentmangerment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    // Read the property from application.yaml or your .env format
    // providing a default fallback to localhost just in case it's missing
    @Value("${FRONTEND_URL}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Split by comma in case you specify multiple URLs
        String[] origins = allowedOrigins.split(",");

        registry.addMapping("/**") // Apply this policy to all endpoints
                .allowedOrigins(origins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allowed HTTP methods
                .allowedHeaders("*") // Allow all requested headers
                .allowCredentials(true) // Allow cookies, session headers, and authorization headers
                .maxAge(3600); // How long the browser should cache the pre-flight response (in seconds)
    }
}

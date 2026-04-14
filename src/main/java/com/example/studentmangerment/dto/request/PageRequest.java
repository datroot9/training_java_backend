package com.example.studentmangerment.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pagination and sorting parameters for list endpoints.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageRequest {
    /** One-based page index. */
    @Min(value = 1, message = "Page number must be at least 1")
    private Integer page = 1;

    /** Number of items per page. */
    @Min(value = 1, message = "Page size must be at least 1")
    private Integer size = 10;

    /** Sort field name (service-specific; {@code null} uses default order). */
    private String sortBy;
    /** Sort direction: {@code asc} or {@code desc}. */
    private String sortDirection = "asc"; // asc or desc
}

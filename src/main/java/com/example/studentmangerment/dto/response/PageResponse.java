package com.example.studentmangerment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Paginated list wrapper for API responses.
 *
 * @param <T> element type in the current page
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {
    /** Items in the current page. */
    private List<T> data;
    /** One-based current page number. */
    private int currentPage;
    /** Page size used for this result. */
    private int pageSize;
    /** Total rows matching the query across all pages. */
    private long totalElements;
    /** Total number of pages. */
    private int totalPages;
    /** Whether a next page exists. */
    private boolean hasNext;
    /** Whether a previous page exists. */
    private boolean hasPrevious;
}

package com.example.studentmangerment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard API envelope for success and error JSON responses.
 *
 * @param <T> type of the {@link #data} payload
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    /** HTTP-style status or application code (e.g. 200, 400). */
    private int code;
    /** Human-readable message. */
    private String message;
    /** Response payload; omitted from JSON when {@code null} if using {@link JsonInclude}. */
    private T data;

    /**
     * Builds a successful response with default message and the given data.
     *
     * @param data payload
     * @param <T> data type
     * @return response with code 200
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message("Success")
                .data(data)
                .build();
    }

    /**
     * Builds a successful response with a custom message.
     *
     * @param message summary message
     * @param data payload (may be {@code null})
     * @param <T> data type
     * @return response with code 200
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Builds an error response with explicit code and message; no data.
     *
     * @param code application or HTTP code
     * @param message error description
     * @param <T> unused type parameter for call-site inference
     * @return error response
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .build();
    }

    /**
     * Builds an error response with HTTP 500 semantics and the given message.
     *
     * @param message error description
     * @param <T> unused type parameter
     * @return error response with code 500
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .code(500)
                .message(message)
                .build();
    }
}

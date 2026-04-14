package com.example.studentmangerment.exception;

import com.example.studentmangerment.dto.response.ApiResponse;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Centralized exception mapping for REST controllers.
 *
 * <p>Converts exceptions into consistent {@link ApiResponse} error payloads.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles Bean Validation errors and returns field-level messages.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        for (ObjectError oe : ex.getBindingResult().getGlobalErrors()) {
            errors.put(oe.getObjectName(), oe.getDefaultMessage());
        }

        String summary = summarizeFieldErrors(ex.getBindingResult().getFieldErrors(),
                ex.getBindingResult().getGlobalErrors());

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(summary)
                .data(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private static String summarizeFieldErrors(List<FieldError> fieldErrors, List<ObjectError> globalErrors) {
        List<String> parts = new ArrayList<>();
        for (FieldError fe : fieldErrors) {
            parts.add(fe.getField() + " — " + fe.getDefaultMessage());
        }
        for (ObjectError ge : globalErrors) {
            parts.add(ge.getObjectName() + " — " + ge.getDefaultMessage());
        }
        if (parts.isEmpty()) {
            return "Invalid input: please check the submitted fields.";
        }
        return "Invalid input: " + String.join("; ", parts);
    }

    /**
     * Handles malformed JSON and date parsing issues.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String detail = describeHttpMessageNotReadable(ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), detail));
    }

    private static String describeHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getMostSpecificCause();
        if (cause instanceof InvalidFormatException ife) {
            String path = ife.getPath().stream()
                    .map(ref -> ref.getFieldName() != null ? ref.getFieldName() : String.valueOf(ref.getIndex()))
                    .filter(s -> s != null && !s.equals("null"))
                    .collect(Collectors.joining("."));
            String field = path.isEmpty() ? "field" : path;
            String target = ife.getTargetType() != null ? ife.getTargetType().getSimpleName() : "value";
            if (cause.getMessage() != null && (cause.getMessage().contains("birthday") || "Date".equals(target))) {
                return "Invalid value for '" + field + "': use date format yyyy/MM/dd (example: 2004/01/15).";
            }
            return "Invalid value for '" + field + "': expected " + target + " (see API docs for allowed formats).";
        }
        if (cause instanceof UnrecognizedPropertyException upe) {
            return "Unknown field '" + upe.getPropertyName() + "' in JSON — remove it or check spelling.";
        }
        if (cause instanceof MismatchedInputException mie) {
            String path = mie.getPath().stream()
                    .map(ref -> ref.getFieldName())
                    .filter(n -> n != null)
                    .collect(Collectors.joining("."));
            String field = path.isEmpty() ? "body" : path;
            if (mie.getTargetType() != null && mie.getTargetType().getSimpleName().contains("Date")) {
                return "Invalid value for '" + field + "': use date format yyyy/MM/dd.";
            }
            return "Invalid JSON for '" + field + "': " + (mie.getMessage() != null
                    ? mie.getMessage() : "type or structure does not match.");
        }
        if (cause instanceof JsonMappingException jme) {
            String path = jme.getPath().stream()
                    .map(ref -> ref.getFieldName())
                    .filter(n -> n != null)
                    .collect(Collectors.joining("."));
            String field = path.isEmpty() ? "request body" : path;
            return "Cannot map JSON to '" + field + "': " + jme.getMessage();
        }
        String msg = ex.getMessage();
        if (msg != null && msg.contains("JSON parse error")) {
            return "Malformed JSON: check commas, brackets, and quotes in the request body.";
        }
        return "Unreadable request body: " + (cause.getMessage() != null ? cause.getMessage() : ex.getMessage());
    }

    /**
     * Maps authorization failures (e.g. non-admin calling admin-only endpoints) to HTTP 403.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), "Access denied"));
    }

    /**
     * Handles query/path parameter type mismatch errors.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        Class<?> requiredType = ex.getRequiredType();
        String typeName = requiredType == null ? "unknown type" : requiredType.getSimpleName();
        String detailMessage = String.format("The parameter '%s' should be of type '%s'", ex.getName(), typeName);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), detailMessage));
    }

    /**
     * Maps missing resource errors to HTTP 404.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    /**
     * Maps duplicate/conflict errors to HTTP 409.
     */
    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleAlreadyExistsException(AlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(HttpStatus.CONFLICT.value(), ex.getMessage()));
    }

    /**
     * Propagates HTTP status from {@link ResponseStatusException} (e.g. unauthorized login).
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleResponseStatusException(ResponseStatusException ex) {
        HttpStatusCode statusCode = ex.getStatusCode();
        int code = statusCode.value();
        String reason = ex.getReason();
        if (reason == null) {
            HttpStatus resolved = HttpStatus.resolve(code);
            reason = resolved != null ? resolved.getReasonPhrase() : "Request failed";
        }
        return ResponseEntity.status(statusCode)
                .body(ApiResponse.error(code, reason));
    }

    /**
     * Business rule violations such as mismatched passwords on register.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    /**
     * Fallback handler for runtime exceptions.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex) {
        ApiResponse<Void> response = ApiResponse.error(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Final fallback for all unexpected checked exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
        ApiResponse<Void> response = ApiResponse.error(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred: " + ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

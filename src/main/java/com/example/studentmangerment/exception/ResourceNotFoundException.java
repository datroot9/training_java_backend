package com.example.studentmangerment.exception;

/**
 * Thrown when a requested entity does not exist (mapped to HTTP 404).
 */
public class ResourceNotFoundException extends RuntimeException {
    /**
     * @param message detail returned to the client
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

package com.example.studentmangerment.exception;

/**
 * Thrown when creating a resource would violate a uniqueness constraint (e.g. duplicate student code).
 */
public class AlreadyExistsException extends RuntimeException {
    /**
     * @param message detail returned to the client (typically HTTP 409)
     */
    public AlreadyExistsException(String message) {
        super(message);
    }
}

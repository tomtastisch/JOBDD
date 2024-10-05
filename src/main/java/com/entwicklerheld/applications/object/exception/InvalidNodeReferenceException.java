package com.entwicklerheld.applications.object.exception;

/**
 * Exception thrown when a node reference cannot be resolved in the OBDD graph.
 */
public class InvalidNodeReferenceException extends RuntimeException {
    public InvalidNodeReferenceException(String message) {
        super(message);
    }

    public InvalidNodeReferenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
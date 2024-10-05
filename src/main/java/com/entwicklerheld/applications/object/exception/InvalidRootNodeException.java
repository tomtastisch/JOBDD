package com.entwicklerheld.applications.object.exception;

/**
 * Exception thrown when there is no valid root node or more than one root node found in the OBDD graph.
 */
public class InvalidRootNodeException extends RuntimeException {
    public InvalidRootNodeException(String message) {
        super(message);
    }

    public InvalidRootNodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
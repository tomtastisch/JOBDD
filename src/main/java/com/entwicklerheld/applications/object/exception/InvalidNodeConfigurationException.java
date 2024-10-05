package com.entwicklerheld.applications.object.exception;

/**
 * Exception thrown when a node in the OBDD graph is improperly configured, missing true/false branches, or parents.
 */
public class InvalidNodeConfigurationException extends RuntimeException {
    public InvalidNodeConfigurationException(String message) {
        super(message);
    }

    public InvalidNodeConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
package com.entwicklerheld.applications.object.exception;

public class NotInitializedException extends ExceptionInInitializerError {
    public NotInitializedException(String message) {
        super(message);
    }
}

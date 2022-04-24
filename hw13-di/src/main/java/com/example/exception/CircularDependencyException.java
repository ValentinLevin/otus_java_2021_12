package com.example.exception;

public class CircularDependencyException extends RuntimeException {
    private static final String MESSAGE_TEMPLATE = "Detects circular dependency of component: %s";
    private static final long serialVersionUID = -2601648594153647980L;

    public CircularDependencyException(String componentName) {
        super(MESSAGE_TEMPLATE.formatted(componentName));
    }
}

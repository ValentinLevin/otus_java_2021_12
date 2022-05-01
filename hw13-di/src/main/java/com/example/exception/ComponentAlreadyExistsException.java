package com.example.exception;

public class ComponentAlreadyExistsException extends RuntimeException {
    private static final String MESSAGE_TEMPLATE = "Already exists component with name %s";
    private static final long serialVersionUID = 6572621345857580376L;

    public ComponentAlreadyExistsException(String name) {
        super(MESSAGE_TEMPLATE.formatted(name));
    }
}

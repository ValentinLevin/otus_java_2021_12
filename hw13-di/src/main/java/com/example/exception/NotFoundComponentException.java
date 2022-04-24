package com.example.exception;

public class NotFoundComponentException extends RuntimeException {
    private static final String MESSAGE_TEMPLATE = "Not found component for class %s";
    private static final long serialVersionUID = 1862731150966173062L;

    public NotFoundComponentException(Class<?> clazz) {
        super(MESSAGE_TEMPLATE.formatted(clazz.getCanonicalName()));
    }
}

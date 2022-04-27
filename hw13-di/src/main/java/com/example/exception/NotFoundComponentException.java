package com.example.exception;

public class NotFoundComponentException extends RuntimeException {
    private static final String NOT_FOUND_BY_CLASS_MESSAGE_TEMPLATE = "Not found component for class %s";
    private static final String NOT_FOUND_BY_NAME_MESSAGE_TEMPLATE = "Not found component by name %s";

    private static final long serialVersionUID = 1862731150966173062L;

    public NotFoundComponentException(Class<?> clazz) {
        super(NOT_FOUND_BY_CLASS_MESSAGE_TEMPLATE.formatted(clazz.getCanonicalName()));
    }

    public NotFoundComponentException(String name) {
        super(NOT_FOUND_BY_NAME_MESSAGE_TEMPLATE.formatted(name));
    }
}

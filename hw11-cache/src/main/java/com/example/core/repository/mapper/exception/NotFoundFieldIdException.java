package com.example.core.repository.mapper.exception;

public class NotFoundFieldIdException extends RuntimeException {
    private static final String MESSAGE_TEMPLATE = "Not found id field in class %s";

    public NotFoundFieldIdException(Class<?> clazz) {
        super(String.format(MESSAGE_TEMPLATE, clazz.getCanonicalName()));
    }
}

package com.example.core.repository.mapper.exception;

public class NotFoundConstructorWithoutParamsException extends RuntimeException {
    private static final String MESSAGE_TEMPLATE = "Not found constructor without params in class %s";

    public NotFoundConstructorWithoutParamsException(Class<?> clazz) {
        super(String.format(MESSAGE_TEMPLATE, clazz.getCanonicalName()));
    }
}

package com.example.exception;

import java.lang.reflect.Method;

public class ObjectInstantiateException extends RuntimeException {
    private static final String MESSAGE_TEMPLATE = "Error on creation of class  instance: %s";

    private static final long serialVersionUID = -1030873508864092502L;
    public ObjectInstantiateException(Class<?> clazz) {
        super(MESSAGE_TEMPLATE.formatted(clazz.getCanonicalName()));
    }
}

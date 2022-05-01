package com.example.exception;

import java.lang.reflect.Method;

public class MethodInvokeException extends RuntimeException {
    private static final String MESSAGE_TEMPLATE = "Error on method %s invoke to %s object";

    private static final long serialVersionUID = 8225920630902332467L;
    public MethodInvokeException(Method method, Object object) {
        super(MESSAGE_TEMPLATE.formatted(method.getName(), object.getClass()));
    }
}

package com.example.exception;

public class NotFoundComponentForMethodParams extends RuntimeException {
    private static final String MESSAGE_TEMPLATE = "Not found component for param with class: %s";
    private static final long serialVersionUID = -2601648594153647980L;

    public NotFoundComponentForMethodParams(Class<?> paramClass) {
        super(MESSAGE_TEMPLATE.formatted(paramClass.getCanonicalName()));
    }
}

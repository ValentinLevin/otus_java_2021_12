package com.example.framework.exceptions;

public class NotEqualsException extends RuntimeException {
    private static final long serialVersionUID = -7862965238538893351L;

    public <T> NotEqualsException(T actualValue, T expectedValue) {
        super(new Throwable(String.format("Expected value \"%s\" is not equals to actual value \"%s\"", expectedValue, actualValue)));
    }
}

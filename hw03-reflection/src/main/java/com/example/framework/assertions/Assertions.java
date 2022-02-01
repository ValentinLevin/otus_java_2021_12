package com.example.framework.assertions;

import com.example.framework.exceptions.NotEqualsException;

import java.util.Objects;

public class Assertions {
    public static <T> void assertEquals(T actualValue, T expectedValue) {
        if (!Objects.equals(actualValue, expectedValue)) {
            throw new NotEqualsException(actualValue, expectedValue);
        }
    }
}

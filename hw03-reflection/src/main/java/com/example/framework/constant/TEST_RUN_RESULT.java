package com.example.framework.constant;

public enum TEST_RUN_RESULT {
    PASSED("Passed"),
    SKIPPED("Skipped"),
    FAILED("Failed");

    private final String name;

    @Override
    public String toString() {
        return this.name;
    }

    TEST_RUN_RESULT(String name) {
        this.name = name;
    }
}

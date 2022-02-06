package com.example.framework.constant;

public enum FAIL_STAGE {
    ON_CONSTRUCT("On class instance creation"),
    ON_BEFORE_EACH("On setup method execution"),
    ON_TEST("On test execution"),
    ON_AFTER_EACH("On after test method execution");

    private final String name;

    @Override
    public String toString() {
        return this.name;
    }

    FAIL_STAGE(String name) {
        this.name = name;
    }
}

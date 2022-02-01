package com.example.framework.runner;

public interface TestRunner {
    void run(Class<?> testClass);
    void run(String testClassName) throws ClassNotFoundException;
}

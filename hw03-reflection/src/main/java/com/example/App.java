package com.example;

import com.example.framework.runner.TestRunner;
import com.example.framework.runner.TestRunnerImpl;

public class App {
    public static void main(String[] args) {
        TestRunner testRunner = new TestRunnerImpl();
        testRunner.run(MyDateTest.class);
    }
}

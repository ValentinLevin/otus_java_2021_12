package com.example.framework.printservice;

import com.example.framework.constant.TEST_RUN_RESULT;
import com.example.framework.dto.TestResult;

import java.util.Collection;

public class SystemOutPrintService implements PrintService {
    @Override
    public void process(Collection<TestResult> testResults) {
        System.out.println("--------------------------------");

        printTotal(testResults);

        System.out.println("--------------------------------");

        for (TestResult testResult: testResults) {
            if (testResult.getRunResult() != TEST_RUN_RESULT.SKIPPED) {
                printTestResult(testResult);
                System.out.println("--------------------------------");
            }
        }
    }

    private void printTotal(Collection<TestResult> testResults) {
        System.out.println("Tests total: " + testResults.size());
        System.out.println("Passed test count: " + getTestResultByTestRunResult(testResults, TEST_RUN_RESULT.PASSED));
        System.out.println("Failed test count: " + getTestResultByTestRunResult(testResults, TEST_RUN_RESULT.FAILED));
        System.out.println("Skipped test count: " + getTestResultByTestRunResult(testResults, TEST_RUN_RESULT.SKIPPED));
    }

    private int getTestResultByTestRunResult(Collection<TestResult> testResults, TEST_RUN_RESULT testRunResult) {
        return testResults
                .stream()
                .reduce(0, (x, y) -> (x + (y.getRunResult() == testRunResult ? 1 : 0)), Integer::sum);
    }

    private void printTestResult(TestResult testResult) {
        System.out.println("Test class: " + testResult.getTestClass().getCanonicalName());
        System.out.println("Method for test: " + testResult.getTestMethod().getName());
        System.out.println("Test result: " + testResult.getRunResult());

        if (testResult.getRunResult() == TEST_RUN_RESULT.FAILED) {
            System.out.println("Failed on stage: " + testResult.getFailedOnStage());
            System.out.println("Failed on method: " + testResult.getFailedOnMethod().getName());
            System.out.println("Thrown exception: " + testResult.getThrownException());
        }
    }
}

package com.example.framework.dto;

import com.example.framework.constant.FAIL_STAGE;
import com.example.framework.constant.TEST_RUN_RESULT;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.lang.reflect.Method;

@Getter
@Setter(AccessLevel.PRIVATE)
@ToString
public class TestResult {
    private Class<?> testClass;
    private Method testMethod;
    private TEST_RUN_RESULT runResult;
    private Method failedOnMethod;
    private Throwable thrownException;
    private FAIL_STAGE failedOnStage;

    public TestResult(
            Class<?> testClass,
            Method testMethod,
            TEST_RUN_RESULT runResult
    ) {
        this.setTestClass(testClass);
        this.setTestMethod(testMethod);
        this.setRunResult(runResult);
    }

    public TestResult(
            Class<?> testClass,
            Method testMethod,
            TEST_RUN_RESULT runResult,
            FAIL_STAGE failedOnStage,
            Method failedOnMethod,
            Throwable thrownException
    ) {
        this.setTestClass(testClass);
        this.setTestMethod(testMethod);
        this.setRunResult(runResult);
        this.setFailedOnMethod(failedOnMethod);
        this.setThrownException(thrownException);
        this.setFailedOnStage(failedOnStage);
    }
}

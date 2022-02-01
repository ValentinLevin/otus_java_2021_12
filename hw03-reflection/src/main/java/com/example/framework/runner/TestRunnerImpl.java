package com.example.framework.runner;

import com.example.framework.annotations.After;
import com.example.framework.annotations.Before;
import com.example.framework.annotations.Test;
import com.example.framework.constant.FAIL_STAGE;
import com.example.framework.constant.TEST_RUN_RESULT;
import com.example.framework.dto.TestResult;
import com.example.framework.exceptions.TestClassIsNullException;
import com.example.framework.printservice.SystemOutPrintService;
import com.example.framework.printservice.PrintService;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class TestRunnerImpl implements TestRunner {
    private final PrintService printService;

    public TestRunnerImpl(PrintService printService) {
        this.printService = printService;
    }

    public TestRunnerImpl() {
        this(new SystemOutPrintService());
    }

    @Override
    public void run(Class<?> testClass) {
        if (testClass == null) {
            throw new TestClassIsNullException();
        }

        Collection<Method> beforeEachTestMethods = this.getMethodList(Before.class, testClass);
        Collection<Method> afterEachTestMethods = this.getMethodList(After.class, testClass);
        Collection<Method> testMethods = this.getMethodList(Test.class, testClass);

        Collection<TestResult> testResults = new ArrayList<>();
        for (Method testMethod: testMethods) {
            if (!isTestMethodDisabled(testMethod)) {
                testResults.add(runTest(testClass, testMethod, beforeEachTestMethods, afterEachTestMethods));
            } else {
                testResults.add(new TestResult(testClass, testMethod, TEST_RUN_RESULT.SKIPPED));
            }
        }

        this.printService.process(testResults);
    }

    @Override
    public void run(String className) throws ClassNotFoundException {
        Class<?> testClass = Class.forName(className);
        this.run(testClass);
    }

    private <T extends Annotation> Collection<Method> getMethodList(Class<T> annotationClass, Class<?> testClass) {
        return Arrays.stream(testClass.getMethods())
                .filter(method -> method.isAnnotationPresent(annotationClass))
                .toList();
    }

    private <T> TestResult runTest(
            Class<T> testClass,
            Method testMethod,
            Collection<Method> beforeEachMethods,
            Collection<Method> afterEachMethods
    ) {
        TestResult testResult = null;

        T testObject;
        try {
            Constructor<T> testClassConstructor = testClass.getDeclaredConstructor();
            testObject = testClassConstructor.newInstance();
        } catch (InvocationTargetException e) {
            return new TestResult(testClass, testMethod, TEST_RUN_RESULT.FAILED, FAIL_STAGE.ON_CONSTRUCT, null, e.getTargetException());
        } catch (Exception e) {
            return new TestResult(testClass, testMethod, TEST_RUN_RESULT.FAILED, FAIL_STAGE.ON_CONSTRUCT, null, e);
        }

        for (Method method : beforeEachMethods) {
            Throwable throwable = invokeMethod(method, testObject);
            if (throwable != null) {
                testResult = new TestResult(testClass, testMethod, TEST_RUN_RESULT.FAILED, FAIL_STAGE.ON_BEFORE_EACH, method, throwable);
                break;
            }
        }

        if (testResult == null) {
            Throwable throwable = invokeMethod(testMethod, testObject);
            if (throwable != null) {
                testResult = new TestResult(testClass, testMethod, TEST_RUN_RESULT.FAILED, FAIL_STAGE.ON_TEST, testMethod, throwable);
            }
        }

        for (Method method: afterEachMethods) {
            Throwable throwable = invokeMethod(method, testObject);
            if (throwable != null && testResult == null) {
                testResult = new TestResult(testClass, testMethod, TEST_RUN_RESULT.FAILED, FAIL_STAGE.ON_AFTER_EACH, method, throwable);
            }
        }

        return testResult == null ? new TestResult(testClass, testMethod, TEST_RUN_RESULT.PASSED) : testResult;
    }

    private Throwable invokeMethod(Method method, Object invokeOnObject) {
        try {
            method.invoke(invokeOnObject);
        } catch (InvocationTargetException e) {
            return e.getTargetException();
        } catch (Exception e) {
            return e;
        }

        return null;
    }

    private boolean isTestMethodDisabled(Method method) {
        return method.getAnnotation(Test.class).disabled();
    }
}

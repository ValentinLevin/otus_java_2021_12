package com.example;

import com.example.framework.constant.TEST_RUN_RESULT;
import com.example.framework.printservice.RawDataPrintService;
import com.example.framework.runner.TestRunner;
import com.example.framework.runner.TestRunnerImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

@DisplayName(value = "Запускалка теста")
class TestRunnerTest {

    @Test
    @DisplayName("Проверка на общее количество найденных методов для тестирования")
    void checkTotalTestCount() {
        RawDataPrintService printService = new RawDataPrintService();
        TestRunner testRunner = new TestRunnerImpl(printService);

        testRunner.run(MyDateTestExample.class);

        int expectedValue = 5;
        int actualValue = printService.getTotalTestCount();

        Assertions.assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    @DisplayName("Проверка на количество не пройденных тестов")
    void checkFailedTestCount() {
        RawDataPrintService printService = new RawDataPrintService();
        TestRunner testRunner = new TestRunnerImpl(printService);

        testRunner.run(MyDateTestExample.class);

        int expectedValue = 2;
        int actualValue = printService.getTestCountByTestRunResult(TEST_RUN_RESULT.FAILED);

        Assertions.assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    @DisplayName("Проверка на количество пропущенных тестов")
    void checkSkippedTestCount() {
        RawDataPrintService printService = new RawDataPrintService();
        TestRunner testRunner = new TestRunnerImpl(printService);

        testRunner.run(MyDateTestExample.class);

        int expectedValue = 1;
        int actualValue = printService.getTestCountByTestRunResult(TEST_RUN_RESULT.SKIPPED);

        Assertions.assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    @DisplayName("Проверка на количество пройденных успешно тестов")
    void checkPassedTestCount() {
        RawDataPrintService printService = new RawDataPrintService();
        TestRunner testRunner = new TestRunnerImpl(printService);

        testRunner.run(MyDateTestExample.class);

        int expectedValue = 2;
        int actualValue = printService.getTestCountByTestRunResult(TEST_RUN_RESULT.FAILED);

        Assertions.assertThat(actualValue).isEqualTo(expectedValue);
    }
}

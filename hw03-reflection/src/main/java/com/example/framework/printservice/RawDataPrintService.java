package com.example.framework.printservice;

import com.example.framework.constant.TEST_RUN_RESULT;
import com.example.framework.dto.TestResult;

import java.util.Collection;

public class RawDataPrintService implements PrintService {
    private Collection<TestResult> testResults;

    @Override
    public void process(Collection<TestResult> testResults) {
        this.testResults = testResults;
    }

    public int getTotalTestCount() {
        return this.testResults == null ? 0 : this.testResults.size();
    }

    public int getTestCountByTestRunResult(TEST_RUN_RESULT testRunResult) {
        if (this.testResults == null) {
            return 0;
        } else {
            return this.testResults.stream()
                    .reduce(0, (x, y) -> x + (y.getRunResult() == testRunResult ? 1 : 0), Integer::sum);
        }
    }
}

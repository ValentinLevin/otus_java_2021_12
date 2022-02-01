package com.example.framework.printservice;

import com.example.framework.dto.TestResult;

import java.util.Collection;

public interface PrintService {
    void process(Collection<TestResult> testResults);
}

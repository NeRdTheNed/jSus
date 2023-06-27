package com.github.NeRdTheNed.jSus.detector;

import java.util.List;

import com.github.NeRdTheNed.jSus.detector.checker.TestResult;

public class CheckResult {

    public final String fileName;
    public final String checkerName;
    public final List<TestResult> checkerResults;

    public CheckResult(String fileName, String checkerName, List<TestResult> checkerResults) {
        this.fileName = fileName;
        this.checkerName = checkerName;
        this.checkerResults = checkerResults;
    }
}

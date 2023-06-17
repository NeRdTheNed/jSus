package com.github.NeRdTheNed.jSus.detector.checker;

public class TestResult {
    public enum TestResultLevel {
        VIRUS,
        STRONG_SUS,
        SUS,
        BENIGN
    }

    public final TestResultLevel result;
    public final String reason;

    public TestResult(TestResultLevel result, String reason) {
        this.result = result;
        this.reason = reason;
    }

}

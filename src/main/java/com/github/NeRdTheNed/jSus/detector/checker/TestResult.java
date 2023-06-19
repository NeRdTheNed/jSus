package com.github.NeRdTheNed.jSus.detector.checker;

public class TestResult {
    public enum TestResultLevel {
        VIRUS("red"),
        STRONG_SUS("magenta"),
        SUS("yellow"),
        BENIGN("blue"),
        VERY_BENIGN("green");

        public final String color;

        TestResultLevel(String color) {
            this.color = color;
        }
    }

    public final TestResultLevel result;
    public final String reason;
    public final int amount;

    public TestResult(TestResultLevel result, String reason, int amount) {
        this.result = result;
        this.reason = reason;
        this.amount = amount;
    }

}

package com.github.NeRdTheNed.jSus.result;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import com.github.NeRdTheNed.jSus.detector.checker.TestResult.TestResultLevel;

public class FileScanResults {
    public boolean errorReadingFile;
    public final Map<TestResultLevel, Map<String, CheckResults>> scanResultGroups;

    public FileScanResults(boolean errorReadingFile, Map<TestResultLevel, Map<String, CheckResults>> scanResultGroups) {
        this.errorReadingFile = errorReadingFile;
        this.scanResultGroups = scanResultGroups;
    }

    public FileScanResults(boolean errorReadingFile) {
        this(errorReadingFile, new EnumMap<>(TestResultLevel.class));
    }

    public FileScanResults() {
        this(false);
    }

    public FileScanResults add(TestResultLevel level, Map<String, CheckResults> res) {
        scanResultGroups.merge(level, res, (existing, toAdd) -> { toAdd.forEach((group, newRes) -> existing.merge(group, newRes, CheckResults::add)); return existing; });
        return this;
    }

    public FileScanResults add(TestResultLevel level, String group, CheckResults res) {
        scanResultGroups.computeIfAbsent(level, k -> new HashMap<>()).merge(group, res, CheckResults::add);
        return this;
    }

    public FileScanResults add(TestResultLevel level, String group, String reason, int amount) {
        scanResultGroups.computeIfAbsent(level, k -> new HashMap<>()).computeIfAbsent(group, k -> new CheckResults()).add(reason, amount);
        return this;
    }

    public FileScanResults add(TestResultLevel level, String group, String reason) {
        return add(level, group, reason, 1);
    }

    public FileScanResults add(Map<TestResultLevel, Map<String, CheckResults>> groups) {
        groups.forEach(this::add);
        return this;
    }

    public FileScanResults add(FileScanResults res) {
        if (res.errorReadingFile) {
            errorReadingFile = true;
        }

        return add(res.scanResultGroups);
    }
}

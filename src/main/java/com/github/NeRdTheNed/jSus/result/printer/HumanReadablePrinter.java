package com.github.NeRdTheNed.jSus.result.printer;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Map.Entry;

import com.github.NeRdTheNed.jSus.detector.checker.TestResult;
import com.github.NeRdTheNed.jSus.detector.checker.TestResult.TestResultLevel;
import com.github.NeRdTheNed.jSus.result.ArchiveScanResults;
import com.github.NeRdTheNed.jSus.result.CheckResults;
import com.github.NeRdTheNed.jSus.result.FileScanResults;

import picocli.CommandLine.Help.Ansi;

public class HumanReadablePrinter extends Printer {
    private final TestResult.TestResultLevel level;
    private final boolean color;

    private static final String FOUND_SUS_ARCH_BASE = "Found sus for archive";
    private static final String FOUND_SUS_ARCH_COL = Ansi.AUTO.string("@|bold,yellow " + FOUND_SUS_ARCH_BASE + "|@ ");
    private static final String FOUND_SUS_ARCH_NOCOL = FOUND_SUS_ARCH_BASE + " ";

    private String getLevelText(TestResult.TestResultLevel level) {
        return color ? Ansi.AUTO.string("@|" + level.color + " " + level + "|@") : level.toString();
    }

    public HumanReadablePrinter(Map<String, ArchiveScanResults> scanResults, TestResultLevel level, boolean color) {
        super(scanResults);
        this.level = level;
        this.color = color;
    }

    public HumanReadablePrinter(String archiveName, ArchiveScanResults scanResult, TestResult.TestResultLevel level, boolean color) {
        super(archiveName, scanResult);
        this.level = level;
        this.color = color;
    }

    private boolean checkLevel(ArchiveScanResults results) {
        return results.errorReadingArchive ||
               results.fileScanResults.values().stream().anyMatch(e -> e.scanResultGroups.keySet().stream().anyMatch(f -> level.ordinal() >= f.ordinal())) ||
               results.recursiveArchiveScanResults.values().stream().anyMatch(this::checkLevel);
    }

    private void print(PrintWriter writer, String archiveName, ArchiveScanResults archiveResults, String formatBase, boolean first) {
        if (checkLevel(archiveResults)) {
            writer.printf(formatBase, (first ? "Results for archive " : "Results for bundled archive ") + archiveName);
            formatBase = "  " + formatBase;
        } else if (first) {
            return;
        }

        if (archiveResults.errorReadingArchive) {
            writer.printf(formatBase, "Error while reading archive " + archiveName + ", scan results may be missing");
        }

        // TODO Good code
        final String indentOne = "  " + formatBase;
        final String indentTwo = "  " + indentOne;
        final String indentThree = "  " + indentTwo;
        final String indentFour = "  " + indentThree;
        archiveResults.recursiveArchiveScanResults.forEach((e, f) -> print(writer, e, f, indentOne, false));
        boolean firstSusLog = true;

        for (final Entry<String, FileScanResults> fileResultGroup : archiveResults.fileScanResults.entrySet()) {
            boolean firstSusLogForFile = true;

            for (final Entry<TestResultLevel, Map<String, CheckResults>> scanGroup : fileResultGroup.getValue().scanResultGroups.entrySet()) {
                final TestResultLevel groupLevel = scanGroup.getKey();

                if (level.ordinal() >= groupLevel.ordinal()) {
                    if (firstSusLog) {
                        writer.printf(formatBase, (color ? FOUND_SUS_ARCH_COL : FOUND_SUS_ARCH_NOCOL) + archiveName);
                        firstSusLog = false;
                    }

                    if (firstSusLogForFile) {
                        writer.printf(indentOne, "Found sus for file " + fileResultGroup.getKey());
                        firstSusLogForFile = false;
                    }

                    writer.printf(indentTwo, "Sus level " + getLevelText(groupLevel));
                    final Map<String, CheckResults> resultMap = scanGroup.getValue();

                    for (final Entry<String, CheckResults> checkEntry : resultMap.entrySet()) {
                        writer.printf(indentThree, "Sus found by checker " + checkEntry.getKey());

                        for (final Entry<String, Integer> scanEntry : checkEntry.getValue().checkResultsMap.entrySet()) {
                            writer.printf(indentFour, "Detected " + scanEntry.getValue() + " time(s): " + scanEntry.getKey());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void print(PrintWriter writer) {
        scanResults.forEach((archiveName, archiveResults) -> print(writer, archiveName, archiveResults, "- %s%n", true));
    }

}

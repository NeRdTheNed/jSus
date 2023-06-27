package com.github.NeRdTheNed.jSus;

import java.io.File;
import java.util.concurrent.Callable;

import com.github.NeRdTheNed.jSus.detector.checker.TestResult;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "jSus", mixinStandardHelpOptions = true, version = "jSus alpha",
         description = "Scans jar files for suspicious behaviour.")
public class CMDMain implements Callable<Integer> {
    @Parameters(index = "0", description = "The file / directory to scan")
    private File file;

    @Option(names = { "--level", "-l" }, defaultValue = "VERY_BENIGN", description = "What level of sus to log. Valid values: ${COMPLETION-CANDIDATES}")
    TestResult.TestResultLevel level;

    @Option(names = { "--verbose", "-v" }, description = "Enable verbose logging. Separate from sus level.")
    boolean verbose = false;

    // TODO Check if color support works on Windows
    @Option(names = { "--colour", "--color", "-c" }, negatable = true, defaultValue = "true", fallbackValue = "true", description = "Enable color output (may not work on Windows).")
    boolean color = true;

    @Option(names = { "--json", "-j" }, description = "Log results as JSON (experimental, format will likely change)")
    boolean json = false;

    @Override
    public Integer call() throws Exception {
        if (!json) {
            System.out.println("jSus: Starting scan of " + file);
        }

        final boolean didSus = Scanner.detectSus(file, verbose, json ? TestResult.TestResultLevel.VERY_BENIGN : level, color, json);

        if (!json) {
            System.out.println("jSus: Finished scan of " + file);
        }

        return didSus ? 1 : CommandLine.ExitCode.OK;
    }

    public static void main(String[] args) {
        final int exitCode = new CommandLine(new CMDMain()).execute(args);
        System.exit(exitCode);
    }
}

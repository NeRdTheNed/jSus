package com.github.NeRdTheNed.jSus;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.jar.JarFile;

import org.objectweb.asm.tree.ClassNode;

import com.github.NeRdTheNed.jSus.detector.CheckResult;
import com.github.NeRdTheNed.jSus.detector.CheckerTask;
import com.github.NeRdTheNed.jSus.detector.checker.Checkers;
import com.github.NeRdTheNed.jSus.detector.checker.IChecker;
import com.github.NeRdTheNed.jSus.detector.checker.TestResult;
import com.github.NeRdTheNed.jSus.result.ArchiveScanResults;
import com.github.NeRdTheNed.jSus.result.FileScanResults;
import com.github.NeRdTheNed.jSus.result.printer.HumanReadablePrinter;
import com.github.NeRdTheNed.jSus.result.printer.JSONPrinter;
import com.github.NeRdTheNed.jSus.util.Util;

// TODO Use LL-zip
public class Scanner {

    public static boolean detectSus(File file, boolean verbose, TestResult.TestResultLevel level, boolean color, boolean json) throws Exception {
        final PrintWriter pw = new PrintWriter(System.out);

        if (file.isDirectory()) {
            return detectSusFromDirectory(file, verbose, level, color, pw, json);
        }

        try
            (final JarFile jarFile = new JarFile(file)) {
            return detectSusFromJar(jarFile, verbose, level, color, pw, json);
        } catch (final Exception e) {
            System.err.println("Invalid directory or jar file " + file.getAbsolutePath());
            throw e;
        }
    }

    public static boolean detectSusFromDirectory(File dir, boolean verbose, TestResult.TestResultLevel level, boolean color, PrintWriter pw, boolean json) {
        if (!dir.isDirectory()) {
            System.err.println("Invalid directory " + dir.getAbsolutePath());
            return false;
        }

        boolean foundSus = false;
        final File[] files = dir.listFiles();

        if (files != null) {
            for (final File file : files) {
                if (file.isDirectory() && detectSusFromDirectory(file, verbose, level, color, pw, json)) {
                    foundSus = true;
                }

                if (!file.toString().toLowerCase().endsWith(".jar")) {
                    continue;
                }

                try
                    (final JarFile jarFile = new JarFile(file)) {
                    if (detectSusFromJar(jarFile, verbose, level, color, pw, json)) {
                        foundSus = true;
                    }
                } catch (final Exception e) {
                    System.err.println("Invalid jar file " + file.getAbsolutePath());
                    e.printStackTrace();
                }
            }
        }

        return foundSus;
    }

    public static boolean detectSusFromJar(JarFile file, boolean verbose, TestResult.TestResultLevel level, boolean color, PrintWriter pw, boolean json) {
        if (verbose && !json) {
            System.out.println("Scanning " + file.getName());
        }

        final ExecutorService execService = Executors.newCachedThreadPool();
        final ExecutorCompletionService<CheckResult> compService = new ExecutorCompletionService<>(execService);
        final List<ClassNode> nodes = Util.gatherClassNodesFromJar(file, verbose, json);
        int tasks = 0;

        for (final IChecker checker : Checkers.checkerList) {
            for (final ClassNode node : nodes) {
                compService.submit(new CheckerTask(checker, node));
                tasks++;
            }
        }

        execService.shutdown();
        boolean didDetectSus = false;
        final ArchiveScanResults archRes = new ArchiveScanResults();

        for (int i = 0; i < tasks; i++) {
            try {
                final Future<CheckResult> result = compService.take();
                final CheckResult finalRes = result.get();

                if (!finalRes.checkerResults.isEmpty()) {
                    didDetectSus = true;

                    for (final TestResult testRes : finalRes.checkerResults) {
                        archRes.add(finalRes.fileName, new FileScanResults().add(testRes.result, finalRes.checkerName, testRes.reason, testRes.amount));
                    }
                }
            } catch (final Exception e) {
                // TODO better error handling
                System.err.println("Exception thrown while running scan task");
                e.printStackTrace();
            }
        }

        if (json) {
            new JSONPrinter(file.getName(), archRes).print(pw);
            pw.println();
        } else {
            new HumanReadablePrinter(file.getName(), archRes, level, color).print(pw);
        }

        pw.flush();

        if (verbose && !json) {
            System.out.println("Finished scanning " + file.getName());
        }

        return didDetectSus;
    }

}

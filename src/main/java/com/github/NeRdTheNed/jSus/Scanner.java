package com.github.NeRdTheNed.jSus;

import java.io.File;
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
import com.github.NeRdTheNed.jSus.util.Util;

import picocli.CommandLine.Help.Ansi;

// TODO Use LL-zip
public class Scanner {

    public static boolean detectSus(File file, boolean verbose, TestResult.TestResultLevel level, boolean color) throws Exception {
        if (file.isDirectory()) {
            return detectSusFromDirectory(file, verbose, level, color);
        }

        try {
            final JarFile jarFile = new JarFile(file);
            return detectSusFromJar(jarFile, verbose, level, color);
        } catch (final Exception e) {
            System.err.println("Invalid directory or jar file " + file.getAbsolutePath());
            throw e;
        }
    }

    public static boolean detectSusFromDirectory(File dir, boolean verbose, TestResult.TestResultLevel level, boolean color) {
        if (!dir.isDirectory()) {
            System.err.println("Invalid directory " + dir.getAbsolutePath());
            return false;
        }

        boolean foundSus = false;
        final File[] files = dir.listFiles();

        if (files != null) {
            for (final File file : files) {
                if (file.isDirectory() && detectSusFromDirectory(file, verbose, level, color)) {
                    foundSus = true;
                }

                if (!file.toString().toLowerCase().endsWith(".jar")) {
                    continue;
                }

                try {
                    final JarFile jarFile = new JarFile(file);

                    if (detectSusFromJar(jarFile, verbose, level, color)) {
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

    public static boolean detectSusFromJar(JarFile file, boolean verbose, TestResult.TestResultLevel level, boolean color) {
        if (verbose) {
            System.out.println("Scanning " + file.getName());
        }

        final ExecutorService execService = Executors.newCachedThreadPool();
        final ExecutorCompletionService<CheckResult> compService = new ExecutorCompletionService<>(execService);
        final List<ClassNode> nodes = Util.gatherClassNodesFromJar(file, verbose);
        int tasks = 0;

        for (final IChecker checker : Checkers.checkerList) {
            for (final ClassNode node : nodes) {
                compService.submit(new CheckerTask(checker, node));
                tasks++;
            }
        }

        execService.shutdown();
        boolean didDetectSus = false;
        boolean firstLog = true;

        for (int i = 0; i < tasks; i++) {
            try {
                final Future<CheckResult> result = compService.take();
                final CheckResult finalRes = result.get();

                if (!finalRes.checkerResults.isEmpty()) {
                    didDetectSus = true;
                    boolean firstCheckerLog = true;

                    for (final TestResult testRes : finalRes.checkerResults) {
                        if (level.ordinal() >= testRes.result.ordinal()) {
                            if (firstLog) {
                                System.out.println((color ? Ansi.AUTO.string("- @|bold,yellow Found sus for file!|@ ") : "- Found sus for file! ") + file.getName());
                                firstLog = false;
                            }

                            if (firstCheckerLog) {
                                System.out.println("  - Sus found by checker " + finalRes.checkerName + "!");
                                firstCheckerLog = false;
                            }

                            System.out.println("    - Sus level " + (color ? Ansi.AUTO.string("@|" + testRes.result.color + " " + testRes.result + "|@") : testRes.result) + " (detected " + testRes.amount + " time(s)): " + testRes.reason);
                        }
                    }
                }
            } catch (final Exception e) {
                // TODO better error handling
                System.err.println("Exception thrown while running scan task");
                e.printStackTrace();
            }
        }

        if (verbose) {
            System.out.println("Finished scanning " + file.getName());
        }

        return didDetectSus;
    }

}

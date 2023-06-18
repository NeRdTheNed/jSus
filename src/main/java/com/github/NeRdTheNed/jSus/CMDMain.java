package com.github.NeRdTheNed.jSus;

import java.io.File;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "jSus", mixinStandardHelpOptions = true, version = "jSus alpha",
         description = "Scans jar files for suspicious behaviour.")
public class CMDMain implements Callable<Integer> {
    @Parameters(index = "0", description = "The file / directory to scan")
    private File file;

    @Override
    public Integer call() throws Exception {
        final boolean didSus = Scanner.detectSus(file);
        return didSus ? 1 : CommandLine.ExitCode.OK;
    }

    public static void main(String[] args) {
        final int exitCode = new CommandLine(new CMDMain()).execute(args);
        System.exit(exitCode);
    }
}

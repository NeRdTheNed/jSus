package com.github.NeRdTheNed.jSus;

import java.io.File;

public class CMDMain {
    public static void main(String[] args) {
        System.out.println("jSus: scans jar files for suspicious behaviour");

        if (args.length < 1) {
            System.err.println("Please specify a file / directory to scan!");
            System.exit(1);
        }

        boolean didSus = false;

        try {
            final File file = new File(args[0]);
            didSus = Scanner.detectSus(file);
        } catch (final Exception e) {
            System.err.println("Invalid path " + args[0] + ", unable to scan file / directory.");
            System.exit(1);
        }

        if (didSus) {
            System.exit(1);
        }
    }
}

package com.github.NeRdTheNed.jSus.result.printer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.NeRdTheNed.jSus.result.ArchiveScanResults;

public class JSONPrinter extends Printer {

    public JSONPrinter(Map<String, ArchiveScanResults> scanResults) {
        super(scanResults);
    }

    public JSONPrinter(String archiveName, ArchiveScanResults scanResult) {
        super(archiveName, scanResult);
    }

    @Override
    public void print(PrintWriter writer) {
        try {
            final String jsonString = new ObjectMapper().writeValueAsString(scanResults);
            writer.write(jsonString);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

}

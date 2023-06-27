package com.github.NeRdTheNed.jSus.result;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class ArchiveScanResults {
    public boolean errorReadingArchive;
    @JsonInclude(value = Include.NON_EMPTY, content = Include.NON_NULL)
    public final Map<String, ArchiveScanResults> recursiveArchiveScanResults;
    public final Map<String, FileScanResults> fileScanResults;

    public ArchiveScanResults(boolean errorReadingArchive, Map<String, ArchiveScanResults> recursiveArchiveScanResults, Map<String, FileScanResults> fileScanResults) {
        this.errorReadingArchive = errorReadingArchive;
        this.recursiveArchiveScanResults = recursiveArchiveScanResults;
        this.fileScanResults = fileScanResults;
    }

    public ArchiveScanResults(Map<String, ArchiveScanResults> recursiveArchiveScanResults, Map<String, FileScanResults> fileScanResults) {
        this(false, recursiveArchiveScanResults, fileScanResults);
    }

    public ArchiveScanResults(boolean errorReadingArchive, Map<String, FileScanResults> fileScanResults) {
        this(errorReadingArchive, new HashMap<>(), fileScanResults);
    }

    public ArchiveScanResults(Map<String, FileScanResults> fileScanResults) {
        this(false, fileScanResults);
    }

    public ArchiveScanResults() {
        this(new HashMap<>());
    }

    public ArchiveScanResults add(String jij, ArchiveScanResults recursiveRes) {
        recursiveArchiveScanResults.merge(jij, recursiveRes, ArchiveScanResults::add);
        return this;
    }

    public ArchiveScanResults addRec(Map<String, ArchiveScanResults> recursiveRes) {
        recursiveRes.forEach(this::add);
        return this;
    }

    public ArchiveScanResults add(String fileName, FileScanResults res) {
        fileScanResults.merge(fileName, res, FileScanResults::add);
        return this;
    }

    public ArchiveScanResults add(Map<String, FileScanResults> res) {
        res.forEach(this::add);
        return this;
    }

    public ArchiveScanResults add(ArchiveScanResults archRes) {
        if (archRes.errorReadingArchive) {
            errorReadingArchive = true;
        }

        return addRec(archRes.recursiveArchiveScanResults).add(archRes.fileScanResults);
    }
}

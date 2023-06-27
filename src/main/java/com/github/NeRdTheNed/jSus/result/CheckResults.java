package com.github.NeRdTheNed.jSus.result;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class CheckResults {
    @JsonIgnore
    public final Map<String, Integer> checkResultsMap;

    @JsonAnyGetter
    private Map<String, Integer> getCheckResultsMap() {
        return checkResultsMap;
    }

    @JsonAnySetter
    private void addToCheckResultsMap(String str, Integer integer) {
        checkResultsMap.put(str, integer);
    }

    public CheckResults(Map<String, Integer> checkResultsMap) {
        this.checkResultsMap = checkResultsMap;
    }

    public CheckResults(String result, int amount) {
        this(new HashMap<>());
        checkResultsMap.put(result, amount);
    }

    public CheckResults(String result) {
        this(result, 1);
    }

    public CheckResults() {
        this(new HashMap<>());
    }

    public CheckResults add(String result, int amount) {
        checkResultsMap.merge(result, amount, Integer::sum);
        return this;
    }

    public CheckResults add(String result) {
        return add(result, 1);
    }

    public CheckResults add(Map<String, Integer> results) {
        results.forEach(this::add);
        return this;
    }

    public CheckResults add(CheckResults result) {
        return add(result.checkResultsMap);
    }
}

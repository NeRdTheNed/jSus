package com.github.NeRdTheNed.jSus.detector.checker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Checkers {

    public static final List<IChecker> checkerList = makeCheckerList();

    private static void addStringCheckers(List<IChecker> list) {
        // TODO Temporary test
        final HashMap<String, TestResult.TestResultLevel> susMap = new HashMap<>();
        susMap.put("I AM THE IMPOSTER", TestResult.TestResultLevel.VIRUS);
        susMap.put("Spell crewmate backwards", TestResult.TestResultLevel.BENIGN);
        final StringChecker susTest = new StringChecker("Amogus", susMap);
        list.add(susTest);
    }

    private static List<IChecker> makeCheckerList() {
        final List<IChecker> list = new ArrayList<>();
        addStringCheckers(list);
        return list;
    }


}

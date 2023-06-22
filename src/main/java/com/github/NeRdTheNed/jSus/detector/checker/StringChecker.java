package com.github.NeRdTheNed.jSus.detector.checker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Pattern;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.github.NeRdTheNed.jSus.detector.checker.TestResult.TestResultLevel;
import com.github.NeRdTheNed.jSus.util.Pair;
import com.github.NeRdTheNed.jSus.util.Util;

public class StringChecker implements IChecker {
    private final String name;
    private final Map<String, TestResult.TestResultLevel> susMap;
    private final Map<Pattern, TestResult.TestResultLevel> susPatternMap;

    public StringChecker(String name, Map<String, TestResult.TestResultLevel> susMap) {
        this(name, susMap, new HashMap<>());
    }

    public StringChecker(String name, Map<String, TestResult.TestResultLevel> susMap, Map<Pattern, TestResult.TestResultLevel> susPatternMap) {
        this.name = name + " string checker";
        this.susMap = susMap;
        this.susPatternMap = susPatternMap;
    }

    private void testString(Map<Pair<String, TestResult.TestResultLevel>, Integer> foundStrings, String toCheck) {
        TestResult.TestResultLevel testResult = susMap.get(toCheck);

        if (testResult == null) {
            // TODO Better code
            final Optional<Entry<Pattern, TestResultLevel>> possible = susPatternMap.entrySet().stream().filter(pattern -> pattern.getKey().matcher(toCheck).find()).findFirst();

            if (possible.isPresent()) {
                testResult = possible.get().getValue();
            }
        }

        if (testResult != null) {
            foundStrings.merge(new Pair<>(toCheck, testResult), 1, Integer::sum);
        }
    }

    @Override
    public List<TestResult> testClass(ClassNode clazz) {
        final List<TestResult> res = new ArrayList<>();
        final Map<Pair<String, TestResult.TestResultLevel>, Integer> foundStrings = new HashMap<>();

        for (final MethodNode methodNode : clazz.methods) {
            for (final AbstractInsnNode ins : methodNode.instructions) {
                final int opcode = ins.getOpcode();

                if (opcode == Opcodes.LDC) {
                    final LdcInsnNode ldc = (LdcInsnNode) ins;

                    if (ldc.cst instanceof String) {
                        final String toCheck = (String) ldc.cst;
                        testString(foundStrings, toCheck);
                    }
                } else {
                    final String possibleString = Util.tryComputeConstantString(ins);

                    if (possibleString != null) {
                        testString(foundStrings, possibleString);
                    }
                }
            }
        }

        foundStrings.forEach((pair, count) -> res.add(new TestResult(pair.v, "String " + pair.k + " found at class " + clazz.name, count)));
        return res;
    }

    @Override
    public String getName() {
        return name;
    }

}

package com.github.NeRdTheNed.jSus.detector.checker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class StringChecker implements IChecker {
    private final String name;
    private final Map<String, TestResult.TestResultLevel> susMap;

    public StringChecker(String name, Map<String, TestResult.TestResultLevel> susMap) {
        this.name = name;
        this.susMap = susMap;
    }

    @Override
    public List<TestResult> testClass(ClassNode clazz) {
        final List<TestResult> res = new ArrayList<>();

        for (final MethodNode methodNode : clazz.methods) {
            for (final AbstractInsnNode ins : methodNode.instructions) {
                final int opcode = ins.getOpcode();

                if (opcode == Opcodes.LDC) {
                    final LdcInsnNode ldc = (LdcInsnNode) ins;

                    if (ldc.cst instanceof String) {
                        final String toCheck = (String) ldc.cst;
                        final TestResult.TestResultLevel testResult = susMap.get(toCheck);

                        if (testResult != null) {
                            res.add(new TestResult(testResult, "String " + toCheck + " found"));
                        }
                    }
                }
            }
        }

        return res;
    }

    @Override
    public String getName() {
        return name;
    }

}

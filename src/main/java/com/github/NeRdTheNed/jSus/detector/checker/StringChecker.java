package com.github.NeRdTheNed.jSus.detector.checker;

import java.util.ArrayList;
import java.util.HashMap;
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
        this.name = name + " string checker";
        this.susMap = susMap;
    }

    private void testString(Map<String, Integer> foundStrings, String toCheck) {
        final TestResult.TestResultLevel testResult = susMap.get(toCheck);

        if (testResult != null) {
            foundStrings.merge(toCheck, 1, Integer::sum);
        }
    }

    @Override
    public List<TestResult> testClass(ClassNode clazz) {
        final List<TestResult> res = new ArrayList<>();
        final Map<String, Integer> foundStrings = new HashMap<>();

        for (final MethodNode methodNode : clazz.methods) {
            for (final AbstractInsnNode ins : methodNode.instructions) {
                final int opcode = ins.getOpcode();

                if (opcode == Opcodes.LDC) {
                    final LdcInsnNode ldc = (LdcInsnNode) ins;

                    if (ldc.cst instanceof String) {
                        final String toCheck = (String) ldc.cst;
                        testString(foundStrings, toCheck);
                    }
                } /* else if (opcode == Opcodes.INVOKESPECIAL) {

                    final MethodInsnNode methodInsNode = (MethodInsnNode) ins;
                    final String methodName = methodInsNode.name;
                    final String methodOwner = methodInsNode.owner;
                    final String methodDesc = methodInsNode.desc;

                    if ("java/lang/String".equals(methodOwner) && "([B)V".equals(methodDesc) && "<init>".equals(methodName)) {
                        // TODO
                    }
                } */
            }
        }

        foundStrings.forEach((k, v) -> res.add(new TestResult(susMap.get(k), "String " + k + " found at class " + clazz.name, v)));
        return res;
    }

    @Override
    public String getName() {
        return name;
    }

}

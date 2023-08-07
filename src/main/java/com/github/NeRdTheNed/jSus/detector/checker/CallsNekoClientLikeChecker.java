package com.github.NeRdTheNed.jSus.detector.checker;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.github.NeRdTheNed.jSus.detector.checker.TestResult.TestResultLevel;

public class CallsNekoClientLikeChecker implements IChecker {

    @Override
    public String getName() {
        return "Calls method with NekoClient-like generated name from <clinit>";
    }

    @Override
    public TestResultLevel getPossibleHighestResult() {
        return TestResult.TestResultLevel.STRONG_SUS;
    }

    @Override
    public List<TestResult> testClass(ClassNode clazz) {
        final List<TestResult> res = new ArrayList<>();

        for (final MethodNode methodNode : clazz.methods) {
            if ("<clinit>".equals(methodNode.name)) {
                for (final AbstractInsnNode ins : methodNode.instructions) {
                    final int opcode = ins.getOpcode();

                    if (opcode == Opcodes.INVOKESTATIC) {
                        final MethodInsnNode methodInsNode = (MethodInsnNode) ins;
                        final String methodName = methodInsNode.name;

                        if ((methodName.length() != 33) || (methodName.charAt(0) != '_')) {
                            continue;
                        }

                        final String methodOwner = methodInsNode.owner;
                        final String methodDesc = methodInsNode.desc;

                        if ("()V".equals(methodDesc) && methodOwner.equals(clazz.name)) {
                            try {
                                final BigInteger part1 = new BigInteger(methodName.substring(1, 17), 16);
                                final BigInteger part2 = new BigInteger(methodName.substring(17, 33), 16);
                                new UUID(part1.longValue(), part2.longValue());
                                // Valid UUID: likely NekoClient
                                res.add(new TestResult(TestResult.TestResultLevel.STRONG_SUS, "Call to method " + methodOwner + "." + methodName + " found at class " + clazz.name, 1));
                            } catch (final Exception e) {
                                // Ignored
                            }
                        }
                    }
                }
            }
        }

        return res;
    }

}

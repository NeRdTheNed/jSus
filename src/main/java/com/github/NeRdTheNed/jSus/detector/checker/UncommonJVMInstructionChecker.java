package com.github.NeRdTheNed.jSus.detector.checker;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import com.github.NeRdTheNed.jSus.detector.checker.TestResult.TestResultLevel;
import com.github.NeRdTheNed.jSus.util.Util;

// I have accidentally created a Kotlin detector, send help
public class UncommonJVMInstructionChecker implements IChecker {

    private static TestResult.TestResultLevel getLevelForNoOpAmount(int amount) {
        if (amount > 200) {
            return TestResult.TestResultLevel.STRONG_SUS;
        }

        if (amount > 100) {
            return TestResult.TestResultLevel.SUS;
        }

        if (amount > 20) {
            return TestResult.TestResultLevel.BENIGN;
        }

        return TestResult.TestResultLevel.VERY_BENIGN;
    }

    @Override
    public String getName() {
        return "Uncommon JVM instruction checker";
    }

    // TODO Check for uses of LDC that Javac wouldn't produce (LDC 1 instead of ICONST_1 ect)
    @Override
    public List<TestResult> testClass(ClassNode clazz) {
        final List<TestResult> res = new ArrayList<>();
        int foundNoOps = 0;

        for (final MethodNode methodNode : clazz.methods) {
            for (final AbstractInsnNode ins : methodNode.instructions) {
                final int opcode = ins.getOpcode();

                // TODO More functionality
                switch (opcode) {
                case Opcodes.NOP:
                    foundNoOps++;
                    break;

                default:
                    break;
                }
            }
        }

        if (foundNoOps > 0) {
            res.add(new TestResult(getLevelForNoOpAmount(foundNoOps), "Found uncommon JVM opcode " + Util.opcodeName(Opcodes.NOP) + " at class " + clazz.name, foundNoOps));
        }

        return res;
    }

    @Override
    public TestResultLevel getPossibleHighestResult() {
        return TestResult.TestResultLevel.STRONG_SUS;
    }

}

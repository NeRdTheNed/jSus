package com.github.NeRdTheNed.jSus.detector.checker;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class UncommonJVMInstructionChecker implements IChecker {

    private static String opcodeName(int opcode) {
        switch (opcode) {
        case Opcodes.NOP:
            return "no-op";

        default:
            return Integer.toString(opcode);
        }
    }

    @Override
    public String getName() {
        return "Uncommon JVM instruction checker";
    }

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
            res.add(new TestResult(TestResult.TestResultLevel.BENIGN, "Found " + foundNoOps + " uncommon JVM opcode(s) " + opcodeName(Opcodes.NOP) + " at class " + clazz.name));
        }

        return res;
    }

}

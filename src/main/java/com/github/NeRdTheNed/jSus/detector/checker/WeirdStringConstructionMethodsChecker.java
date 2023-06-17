package com.github.NeRdTheNed.jSus.detector.checker;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class WeirdStringConstructionMethodsChecker implements IChecker {

    @Override
    public String getName() {
        return "Weird string construction methods checker";
    }

    // TODO More through check, check fixed Base64 as well
    @Override
    public List<TestResult> testClass(ClassNode clazz) {
        final List<TestResult> res = new ArrayList<>();

        for (final MethodNode methodNode : clazz.methods) {
            for (final AbstractInsnNode ins : methodNode.instructions) {
                final int opcode = ins.getOpcode();

                if (opcode == Opcodes.INVOKESPECIAL) {
                    final MethodInsnNode methodInsNode = (MethodInsnNode) ins;
                    final String methodName = methodInsNode.name;
                    final String methodOwner = methodInsNode.owner;
                    final String methodDesc = methodInsNode.desc;

                    if ("java/lang/String".equals(methodOwner) && "([B)V".equals(methodDesc) && "<init>".equals(methodName)) {
                        final AbstractInsnNode prev = ins.getPrevious();

                        if (prev.getOpcode() == Opcodes.BASTORE) {
                            res.add(new TestResult(TestResult.TestResultLevel.SUS, "Constructing String from fixed byte array at class " + clazz.name));
                        }
                    }
                }
            }
        }

        return res;
    }

}

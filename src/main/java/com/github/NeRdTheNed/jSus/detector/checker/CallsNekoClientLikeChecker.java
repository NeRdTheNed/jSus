package com.github.NeRdTheNed.jSus.detector.checker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class CallsNekoClientLikeChecker implements IChecker {

    @Override
    public String getName() {
        return "Calls method with NekoClient-like generated name from <clinit>";
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

                        if ((methodName.length() != 33) || !methodName.startsWith("_")) {
                            continue;
                        }

                        final String methodOwner = methodInsNode.owner;
                        final String methodDesc = methodInsNode.desc;

                        if ("()V".equals(methodDesc) && methodOwner.equals(clazz.name)) {
                            try {
                                final String UUIDLike = methodName.substring(1).replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5");
                                UUID.fromString(UUIDLike);
                                // Valid UUID: likely NekoClient
                                res.add(new TestResult(TestResult.TestResultLevel.STRONG_SUS, "Call to method " + methodOwner + "." + methodName + " found at class " + clazz.name));
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

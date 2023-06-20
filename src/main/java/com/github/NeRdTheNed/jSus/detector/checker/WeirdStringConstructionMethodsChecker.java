package com.github.NeRdTheNed.jSus.detector.checker;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.github.NeRdTheNed.jSus.util.Util;

public class WeirdStringConstructionMethodsChecker implements IChecker {
    private static final Base64.Decoder decoder = Base64.getDecoder();

    @Override
    public String getName() {
        return "Weird string construction methods checker";
    }

    // TODO More thorough check, check for any use of fixed Base64 as well
    @Override
    public List<TestResult> testClass(ClassNode clazz) {
        final List<TestResult> res = new ArrayList<>();
        int foundFixedByteArrayConstructions = 0;

        for (final MethodNode methodNode : clazz.methods) {
            for (final AbstractInsnNode ins : methodNode.instructions) {
                final int opcode = ins.getOpcode();

                if (opcode == Opcodes.INVOKESPECIAL) {
                    final MethodInsnNode methodInsNode = (MethodInsnNode) ins;
                    final String methodName = methodInsNode.name;
                    final String methodOwner = methodInsNode.owner;
                    final String methodDesc = methodInsNode.desc;

                    if ("java/lang/String".equals(methodOwner) && "([B)V".equals(methodDesc) && "<init>".equals(methodName)) {
                        // TODO Handle more scenarios
                        final AbstractInsnNode prev = ins.getPrevious();

                        if (prev != null) {
                            final int previousOpcode = prev.getOpcode();

                            if (previousOpcode == Opcodes.BASTORE) {
                                final String possibleString = Util.tryComputeConstantString(ins);

                                if (possibleString == null) {
                                    foundFixedByteArrayConstructions++;
                                } else {
                                    res.add(new TestResult(TestResult.TestResultLevel.STRONG_SUS, "Constructing String " + possibleString +  " from fixed byte array at class " + clazz.name, 1));
                                }
                            } else if (previousOpcode == Opcodes.INVOKEVIRTUAL) {
                                // TODO Handle previous operations like concat
                                final MethodInsnNode prevMethodInsNode = (MethodInsnNode) prev;
                                final String prevMethodName = prevMethodInsNode.name;
                                final String prevMethodOwner = prevMethodInsNode.owner;
                                final String prevMethodDesc = prevMethodInsNode.desc;

                                if (Util.isCommonBase64DecodeMethod(previousOpcode, prevMethodOwner, prevMethodName, prevMethodDesc)) {
                                    final String possibleString = Util.tryComputeConstantString(prev.getPrevious());

                                    if (possibleString != null) {
                                        try {
                                            final String decoded = new String(decoder.decode(possibleString));
                                            res.add(new TestResult(TestResult.TestResultLevel.STRONG_SUS, "Constructing String from fixed Base64 " + possibleString + " (decoded: " + decoded + ") at class " + clazz.name, 1));
                                        } catch (final IllegalArgumentException e) {
                                            res.add(new TestResult(TestResult.TestResultLevel.STRONG_SUS, "Constructing String from invalid (?) fixed Base64 " + possibleString + " at class " + clazz.name, 1));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (foundFixedByteArrayConstructions > 0) {
            res.add(new TestResult(TestResult.TestResultLevel.STRONG_SUS, "Constructing unknown String from fixed byte array at class " + clazz.name, foundFixedByteArrayConstructions));
        }

        return res;
    }

}

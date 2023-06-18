package com.github.NeRdTheNed.jSus.detector.checker;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class CallsMethodChecker implements IChecker {

    private final int compareOpcode;
    private final String compareMethodOwner;
    private final String compareMethodName;
    private final String compareMethodDesc;

    private final String name;

    private final TestResult.TestResultLevel result;

    private final boolean compareOpcodeMatchWilcard(int toComp) {
        if ((compareOpcode <= Opcodes.INVOKEINTERFACE) && (compareOpcode >= Opcodes.INVOKEVIRTUAL)) {
            return compareOpcode == toComp;
        }

        return ((toComp <= Opcodes.INVOKEINTERFACE) && (toComp >= Opcodes.INVOKEVIRTUAL));
    }

    public CallsMethodChecker(int compareOpcode, String compareMethodOwner, String compareMethodName, String compareMethodDesc, TestResult.TestResultLevel result) {
        this.compareOpcode = compareOpcode;
        this.compareMethodOwner = compareMethodOwner;
        this.compareMethodName = compareMethodName;
        this.compareMethodDesc = compareMethodDesc;
        this.result = result;
        name = "Calls " + (compareMethodOwner == null ? "*" : compareMethodOwner) + "." + (compareMethodName == null ? "*" : compareMethodName) + " checker";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<TestResult> testClass(ClassNode clazz) {
        final List<TestResult> res = new ArrayList<>();

        for (final MethodNode methodNode : clazz.methods) {
            for (final AbstractInsnNode ins : methodNode.instructions) {
                final int opcode = ins.getOpcode();

                if (compareOpcodeMatchWilcard(opcode)) {
                    final MethodInsnNode methodInsNode = (MethodInsnNode) ins;
                    final String methodOwner = methodInsNode.owner;
                    final String methodName = methodInsNode.name;
                    final String methodDesc = methodInsNode.desc;

                    if (((compareMethodName == null) || compareMethodName.equals(methodName)) &&
                            ((compareMethodDesc == null) || compareMethodDesc.equals(methodDesc)) &&
                            ((compareMethodOwner == null) || compareMethodOwner.equals(methodOwner))) {
                        res.add(new TestResult(result, "Call to method " + methodOwner + "." + methodName + " found at class " + clazz.name));
                    }
                }
            }
        }

        return res;
    }

}

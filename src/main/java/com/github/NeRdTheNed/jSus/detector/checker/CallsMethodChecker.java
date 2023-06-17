package com.github.NeRdTheNed.jSus.detector.checker;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class CallsMethodChecker implements IChecker {

    final private int compareOpcode;
    final private String compareMethodOwner;
    final private String compareMethodName;
    final private String compareMethodDesc;

    final private String name;

    final private TestResult.TestResultLevel result;

    public CallsMethodChecker(int compareOpcode, String compareMethodOwner, String compareMethodName, String compareMethodDesc, TestResult.TestResultLevel result) {
        assert ((compareOpcode <= Opcodes.INVOKEINTERFACE) && (compareOpcode >= Opcodes.INVOKEVIRTUAL));
        this.compareOpcode = compareOpcode;
        this.compareMethodOwner = compareMethodOwner;
        this.compareMethodName = compareMethodName;
        this.compareMethodDesc = compareMethodDesc;
        this.result = result;
        name = "Calls " + compareMethodOwner + "." + compareMethodName + " checker";
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

                if (opcode == compareOpcode) {
                    final MethodInsnNode methodInsNode = (MethodInsnNode) ins;
                    final String methodOwner = methodInsNode.owner;
                    final String methodName = methodInsNode.name;
                    final String methodDesc = methodInsNode.desc;

                    if (compareMethodName.equals(methodName) &&
                            compareMethodDesc.equals(methodDesc) &&
                            compareMethodOwner.equals(methodOwner)) {
                        res.add(new TestResult(result, "Call to method " + compareMethodOwner + "." + compareMethodName + " found at class " + clazz.name));
                    }
                }
            }
        }

        return res;
    }

}

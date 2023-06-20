package com.github.NeRdTheNed.jSus.detector.checker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import com.github.NeRdTheNed.jSus.util.Util;

// TODO Doesn't really do much
public class ObfuscatorChecker implements IChecker {
    private static final Set<String> commonObfNamesList = getCommonObfNamesList();
    private static final Set<String> commonObfNamesListCaseSensitive = getCommonObfNamesListCaseSensitive();

    private static Set<String> getCommonObfNamesList() {
        final Set<String> set = new HashSet<>();
        set.add("con");
        set.add("prn");
        set.add("aux");
        set.add("nul");
        return set;
    }

    private static Set<String> getCommonObfNamesListCaseSensitive() {
        final Set<String> set = new HashSet<>();
        final String[] reservedJavaNames = {
            "abstract",
            "assert",
            "boolean",
            "break",
            "byte",
            "case",
            "catch",
            "char",
            "class",
            "const",
            "continue",
            "default",
            "do",
            "double",
            "else",
            "enum",
            "extends",
            "final",
            "finally",
            "float",
            "for",
            "goto",
            "if",
            "implements",
            "import",
            "instanceof",
            "int",
            "interface",
            "long",
            "native",
            "new",
            "package",
            "private",
            "protected",
            "public",
            "return",
            "short",
            "static",
            "strictfp",
            "super",
            "switch",
            "synchronized",
            "this",
            "throw",
            "throws",
            "transient",
            "try",
            "void",
            "volatile",
            "while",
            "_",
            "true",
            "false",
            "null",
        };
        Collections.addAll(set, reservedJavaNames);
        return set;
    }

    private static boolean isChainable(int opcode) {
        switch (opcode) {
        //case Opcodes.NOP:
        case Opcodes.INEG:
        case Opcodes.SWAP:
            return true;

        default:
            return false;
        }
    }

    @Override
    public String getName() {
        return "Obfuscator checker";
    }

    // TODO https://github.com/java-deobfuscator/deobfuscator/tree/master/src/main/java/com/javadeobfuscator/deobfuscator/rules
    // TODO Handle cases like foo$illegalname$bar
    // TODO Check for sequences of bytecode Javac wouldn't produce
    // TODO https://docs.oracle.com/javase/8/docs/api/java/lang/Character.html#isJavaIdentifierStart-int-
    @Override
    public List<TestResult> testClass(ClassNode clazz) {
        final List<TestResult> res = new ArrayList<>();
        final String className = clazz.name;
        final String processedClassName = className.substring(className.lastIndexOf("/") + 1);

        if (processedClassName.isEmpty() || !Character.isJavaIdentifierStart(processedClassName.charAt(0))) {
            res.add(new TestResult(TestResult.TestResultLevel.BENIGN, "Found common obfuscated classname technique at class " + className, 1));
        } else if ((processedClassName.length() == 1) || commonObfNamesListCaseSensitive.contains(processedClassName) || commonObfNamesList.contains(processedClassName.toLowerCase())) {
            res.add(new TestResult(TestResult.TestResultLevel.BENIGN, "Found common obfuscated classname " + className, 1));
        } else if (processedClassName.length() == 2) {
            res.add(new TestResult(TestResult.TestResultLevel.VERY_BENIGN, "Class name may be obfuscated " + className, 1));
        }

        final Map<Integer, Integer> chains = new HashMap<>();

        for (final MethodNode methodNode : clazz.methods) {
            if (methodNode.name.contains("ALLATORIxDEMO")) {
                res.add(new TestResult(TestResult.TestResultLevel.BENIGN, "Allatori demo detected at class " + className, 1));
            }

            boolean foundChain = false;
            int prevOpcode = -1;

            for (final AbstractInsnNode ins : methodNode.instructions) {
                final int opcode = ins.getOpcode();

                if (opcode != prevOpcode) {
                    foundChain = false;
                } else if (isChainable(opcode) && !foundChain) {
                    foundChain = true;
                    chains.merge(opcode, 1, Integer::sum);
                }

                prevOpcode = opcode;
            }
        }

        chains.forEach((k, v) -> res.add(new TestResult(TestResult.TestResultLevel.BENIGN, "Unlikely opcode chain of " + Util.opcodeName(k) + " found at " + className, v)));
        return res;
    }

}

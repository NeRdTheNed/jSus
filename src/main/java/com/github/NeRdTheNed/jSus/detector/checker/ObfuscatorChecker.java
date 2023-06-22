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

    private static boolean checkChains(int opcode) {
        switch (opcode) {
        case Opcodes.NOP:
        case Opcodes.DUP:
        case Opcodes.POP:
        case Opcodes.INEG:
        case Opcodes.LNEG:
        case Opcodes.FNEG:
        case Opcodes.DNEG:
        case Opcodes.SWAP:
        case Opcodes.I2L:
        case Opcodes.I2F:
        case Opcodes.I2D:
        case Opcodes.L2I:
        case Opcodes.L2F:
        case Opcodes.L2D:
        case Opcodes.F2I:
        case Opcodes.F2L:
        case Opcodes.F2D:
        case Opcodes.D2I:
        case Opcodes.D2L:
        case Opcodes.D2F:
        case Opcodes.I2B:
        case Opcodes.I2C:
        case Opcodes.I2S:
            return true;

        default:
            return false;
        }
    }

    private static int chainSize(int opcode) {
        switch (opcode) {
        case Opcodes.NOP:
            return 3;

        case Opcodes.DUP:
        case Opcodes.POP:
            return 2;

        default:
            return 1;
        }
    }

    @Override
    public String getName() {
        return "Obfuscator checker";
    }

    private static void checkName(String name, boolean isClassName, String className, Map<String, Integer> foundBenign, Map<String, Integer> foundVeryBenign) {
        // TODO Find a better balance
        final boolean checkVeryShortNameLength = isClassName;
        final boolean checkShortNameLength = isClassName;
        final char firstChar;
        final boolean notValidJavaStart = name.isEmpty() || (!Character.isJavaIdentifierStart(firstChar = name.charAt(0)) && (firstChar != '-'));

        if (!isClassName && notValidJavaStart && ("<init>".equals(name) || "<clinit>".equals(name))) {
            return;
        }

        if (notValidJavaStart) {
            final String str = isClassName ? "Found common obfuscated classname technique at class " + className : "Found common obfuscated method name technique for method " + name + " at class " + className;
            foundBenign.merge(str, 1, Integer::sum);
        } else if ((checkVeryShortNameLength && (name.length() == 1)) || commonObfNamesListCaseSensitive.contains(name) || commonObfNamesList.contains(name.toLowerCase())) {
            final String str = isClassName ? "Found common obfuscated classname " + className : "Found common obfuscated method name " + name + " at class " + className;
            foundBenign.merge(str, 1, Integer::sum);
        } else if (checkShortNameLength && (name.length() == 2)) {
            final String str = isClassName ? "Class name may be obfuscated " + className : "Method name " + name + " may be obfuscated at class " + className;
            foundVeryBenign.merge(str, 1, Integer::sum);
        }
    }

    // TODO https://github.com/java-deobfuscator/deobfuscator/tree/master/src/main/java/com/javadeobfuscator/deobfuscator/rules
    // TODO Handle cases like foo$illegalname$bar
    // TODO Check for sequences of bytecode Javac wouldn't produce
    // TODO https://docs.oracle.com/javase/8/docs/api/java/lang/Character.html#isJavaIdentifierStart-int-
    @Override
    public List<TestResult> testClass(ClassNode clazz) {
        final List<TestResult> res = new ArrayList<>();
        final Map<String, Integer> foundBenign = new HashMap<>();
        final Map<String, Integer> foundVeryBenign = new HashMap<>();
        final String className = clazz.name;
        final String processedClassName = className.substring(className.lastIndexOf("/") + 1);
        checkName(processedClassName, true, className, foundBenign, foundVeryBenign);
        final Map<Integer, Integer> chains = new HashMap<>();
        int allatoriDemoCount = 0;

        for (final MethodNode methodNode : clazz.methods) {
            if (methodNode.name.contains("ALLATORIxDEMO")) {
                allatoriDemoCount++;
            } else {
                checkName(methodNode.name, false, className, foundBenign, foundVeryBenign);
            }

            boolean foundChain = false;
            int currentChainSize = 0;
            int prevOpcode = -1;

            for (final AbstractInsnNode ins : methodNode.instructions) {
                final int opcode = ins.getOpcode();

                if (opcode != prevOpcode) {
                    foundChain = false;
                    currentChainSize = 0;
                } else if (checkChains(opcode) && !foundChain) {
                    currentChainSize++;

                    if (currentChainSize >= chainSize(opcode)) {
                        foundChain = true;
                        chains.merge(opcode, 1, Integer::sum);
                    }
                }

                prevOpcode = opcode;
            }
        }

        if (allatoriDemoCount > 0) {
            res.add(new TestResult(TestResult.TestResultLevel.BENIGN, "Allatori demo detected at class " + className, allatoriDemoCount));
        }

        foundBenign.forEach((k, v) -> res.add(new TestResult(TestResult.TestResultLevel.BENIGN, k, v)));
        foundVeryBenign.forEach((k, v) -> res.add(new TestResult(TestResult.TestResultLevel.VERY_BENIGN, k, v)));
        chains.forEach((k, v) -> res.add(new TestResult(TestResult.TestResultLevel.BENIGN, "Unlikely opcode chain of " + Util.opcodeName(k) + " found at " + className, v)));
        return res;
    }

}

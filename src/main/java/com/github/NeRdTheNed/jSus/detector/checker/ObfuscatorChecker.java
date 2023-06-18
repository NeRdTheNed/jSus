package com.github.NeRdTheNed.jSus.detector.checker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;

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
        // Real funny guys >:(
        set.add("<html><img src=\"http:\\\">");
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

    @Override
    public String getName() {
        return "Obfuscator checker";
    }

    // TODO https://github.com/java-deobfuscator/deobfuscator/tree/master/src/main/java/com/javadeobfuscator/deobfuscator/rules
    @Override
    public List<TestResult> testClass(ClassNode clazz) {
        final List<TestResult> res = new ArrayList<>();
        final String className = clazz.name;
        final String processedClassName = className.substring(className.lastIndexOf("/") + 1);

        if (commonObfNamesListCaseSensitive.contains(processedClassName) || commonObfNamesList.contains(processedClassName.toLowerCase())) {
            res.add(new TestResult(TestResult.TestResultLevel.BENIGN, "Found common obfuscated classname " + className));
        }

        return res;
    }

}

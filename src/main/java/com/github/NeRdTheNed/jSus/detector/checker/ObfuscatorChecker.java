package com.github.NeRdTheNed.jSus.detector.checker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;

// TODO Doesn't really do much
public class ObfuscatorChecker implements IChecker {
    private static final Set<String> commonObfNamesList = getCommonObfNamesList();

    private static Set<String> getCommonObfNamesList() {
        final Set<String> set = new HashSet<>();
        set.add("con");
        set.add("prn");
        set.add("aux");
        set.add("nul");
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

        if (commonObfNamesList.contains(processedClassName.toLowerCase())) {
            res.add(new TestResult(TestResult.TestResultLevel.BENIGN, "Found common obfuscated classname " + className));
        }

        return res;
    }

}

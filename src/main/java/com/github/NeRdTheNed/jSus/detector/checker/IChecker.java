package com.github.NeRdTheNed.jSus.detector.checker;

import java.util.List;

import org.objectweb.asm.tree.ClassNode;

public interface IChecker {

    String getName();

    List<TestResult> testClass(ClassNode clazz);
}

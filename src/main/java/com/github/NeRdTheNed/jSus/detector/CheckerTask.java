package com.github.NeRdTheNed.jSus.detector;

import java.util.concurrent.Callable;

import org.objectweb.asm.tree.ClassNode;

import com.github.NeRdTheNed.jSus.detector.checker.IChecker;

public class CheckerTask implements Callable<CheckResult> {

    public final IChecker checker;

    public final ClassNode clazz;

    public CheckerTask(IChecker checker, ClassNode clazz) {
        this.checker = checker;
        this.clazz = clazz;
    }

    @Override
    public CheckResult call() throws Exception {
        return new CheckResult(clazz.name + ".class", checker.getName(), checker.testClass(clazz));
    }

}

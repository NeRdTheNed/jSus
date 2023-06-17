package com.github.NeRdTheNed.jSus.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

public class Util {
    public static ClassNode streamToClass(InputStream stream) throws IOException {
        final ClassReader reader = new ClassReader(stream);
        final ClassNode node = new ClassNode();

        try {
            reader.accept(node, 0);
        } catch (final Exception e) {
            return null;
        }

        return node;
    }

    private static void findAddNodes(JarFile jarFile, List<ClassNode> nodes) {
        for (final Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
            final JarEntry entry = entries.nextElement();

            if (entry.isDirectory()) {
                continue;
            }

            final String name = entry.getName();

            if (name.endsWith(".jar")) {
                System.err.println("TODO: JIJ support");
            }

            if (name.endsWith(".class")) {
                try
                    (InputStream is = jarFile.getInputStream(entry)) {
                    final ClassNode node = streamToClass(is);

                    if (node != null) {
                        nodes.add(node);
                    }
                } catch (final IOException e) {
                    System.err.println("Error reading class " + entry.getName() + " from jar " + jarFile.getName());
                    e.printStackTrace();
                }
            }
        }
    }

    public static List<ClassNode> gatherClassNodesFromJar(JarFile jarFile) {
        final List<ClassNode> nodes = new ArrayList<>();
        findAddNodes(jarFile, nodes);
        return nodes;
    }
}

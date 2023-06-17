package com.github.NeRdTheNed.jSus.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
                System.out.println("Adding JIJ " + name + " to scan");
                Path tempFile = null;

                try {
                    // TODO Not totally sure if this is correct
                    tempFile = Files.createTempFile(null, null);
                    tempFile.toFile().deleteOnExit();
                    Files.copy(jarFile.getInputStream(entry), tempFile, StandardCopyOption.REPLACE_EXISTING);
                    final JarFile jij = new JarFile(tempFile.toFile());
                    findAddNodes(jij, nodes);
                } catch (final Exception e) {
                    System.err.println("Issue extracting JIJ to temporary file");
                    e.printStackTrace();
                }
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

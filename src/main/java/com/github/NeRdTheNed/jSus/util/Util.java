package com.github.NeRdTheNed.jSus.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.Printer;

public class Util {
    public static ClassNode streamToClass(InputStream stream, String name) throws IOException {
        final ClassReader reader = new ClassReader(stream);
        final ClassNode node = new ClassNode();

        try {
            reader.accept(node, 0);
        } catch (final Exception e) {
            System.err.println("Malformed class " + name);
            e.printStackTrace();
            return null;
        }

        return node;
    }

    private static void findAddNodes(JarFile jarFile, List<ClassNode> nodes, boolean verbose) {
        String obf = null;
        boolean didFindWeirdObf = false;

        for (final Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
            final JarEntry entry = entries.nextElement();
            final String name = entry.getName();
            final boolean weirdClassObf = name.endsWith(".class/") && ((entry.getCompressedSize() > 0) || (entry.getSize() > 0));

            if (entry.isDirectory() && !weirdClassObf) {
                continue;
            }

            if (weirdClassObf) {
                didFindWeirdObf = true;
            }

            if (name.endsWith(".jar")) {
                if (verbose) {
                    System.out.println("- Adding JIJ " + name + " to scan");
                }

                Path tempFile = null;

                try
                    (InputStream is = jarFile.getInputStream(entry)) {
                    // TODO Not totally sure if this is correct
                    tempFile = Files.createTempFile(null, null);
                    tempFile.toFile().deleteOnExit();
                    Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);
                    final JarFile jij = new JarFile(tempFile.toFile());
                    findAddNodes(jij, nodes, verbose);
                } catch (final Exception e) {
                    System.err.println("Issue extracting JIJ " + name + " from " + jarFile.getName() + " to temporary file");
                    e.printStackTrace();
                }
            }

            if (weirdClassObf || name.endsWith(".class")) {
                try
                    (InputStream is = jarFile.getInputStream(entry)) {
                    final ClassNode node = streamToClass(is, name);

                    if (node != null) {
                        nodes.add(node);
                    } else {
                        System.err.println("Class node was null: " + name);
                    }
                } catch (final IOException e) {
                    System.err.println("Error reading class " + entry.getName() + " from jar " + jarFile.getName());
                    e.printStackTrace();
                }
            }
        }

        if (obf == null) {
            try {
                final Manifest jarManifest = jarFile.getManifest();

                if (jarManifest != null) {
                    final Attributes attrib = jarManifest.getMainAttributes();
                    obf = attrib.getValue("Obfuscated-By");

                    if (obf == null) {
                        obf = attrib.getValue("Protected-By");
                    }
                }
            } catch (final IOException e) {
                System.err.println("Could not get manifest for jar " + jarFile.getName());
                e.printStackTrace();
            }
        }

        if (obf != null) {
            System.out.println("- Note: jar " + jarFile.getName() + " claims to be obfuscated by " + obf);
        }

        if (didFindWeirdObf) {
            System.out.println("- Note: Common class obfuscation technique was used in jar " + jarFile.getName());
        }
    }

    public static List<ClassNode> gatherClassNodesFromJar(JarFile jarFile, boolean verbose) {
        final List<ClassNode> nodes = new ArrayList<>();
        findAddNodes(jarFile, nodes, verbose);
        return nodes;
    }

    public static String opcodeName(int opcode) {
        return ((opcode >= 0) && (opcode < Printer.OPCODES.length)) ? Printer.OPCODES[opcode] : Integer.toString(opcode);
    }

    public static boolean isOpcodeMethodInvoke(int opcode) {
        return ((opcode <= Opcodes.INVOKEINTERFACE) && (opcode >= Opcodes.INVOKEVIRTUAL));
    }

    public static boolean isCommonBase64DecodeMethod(int opcode, String owner, String name, String signature) {
        // TODO handle org.apache.commons.codec.binary.Base16
        // TODO handle org.apache.commons.codec.binary.Base32
        // TODO handle org.apache.commons.codec.binary.Base64
        // TODO handle org.apache.commons.codec.binary.Hex
        if ("java/util/Base64$Decoder".equals(owner) && "decode".equals(name) && "(Ljava/lang/String;)[B".equals(signature)) {
            return true;
        }

        return false;
    }
}

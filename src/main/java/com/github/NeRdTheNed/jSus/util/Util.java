package com.github.NeRdTheNed.jSus.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
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

        if ("javax/xml/bind/DatatypeConverter".equals(owner) && "parseBase64Binary".equals(name) && "(Ljava/lang/String;)[B".equals(signature)) {
            return true;
        }

        return false;
    }

    private static Number getValueOrNull(AbstractInsnNode load) {
        switch (load.getOpcode()) {
        case Opcodes.ICONST_M1:
            return Integer.valueOf(-1);

        case Opcodes.ICONST_0:
            return Integer.valueOf(0);

        case Opcodes.ICONST_1:
            return Integer.valueOf(1);

        case Opcodes.ICONST_2:
            return Integer.valueOf(2);

        case Opcodes.ICONST_3:
            return Integer.valueOf(3);

        case Opcodes.ICONST_4:
            return Integer.valueOf(4);

        case Opcodes.ICONST_5:
            return Integer.valueOf(5);

        case Opcodes.LCONST_0:
            return Long.valueOf(0);

        case Opcodes.LCONST_1:
            return Long.valueOf(1);

        case Opcodes.FCONST_0:
            return Float.valueOf(0f);

        case Opcodes.FCONST_1:
            return Float.valueOf(1f);

        case Opcodes.FCONST_2:
            return Float.valueOf(2f);

        case Opcodes.DCONST_0:
            return Double.valueOf(0d);

        case Opcodes.DCONST_1:
            return Double.valueOf(1d);

        case Opcodes.BIPUSH:
        case Opcodes.SIPUSH:
            return Integer.valueOf(((IntInsnNode) load).operand);

        case Opcodes.LDC:
            final LdcInsnNode ldc = (LdcInsnNode) load;

            if (ldc.cst instanceof Number) {
                return (Number) ldc.cst;
            }

        // Fall through

        default:
            return null;
        }
    }

    private static class Pair<K, V> {
        public final K k;
        public final V v;
        public Pair(K k, V v) {
            this.k = k;
            this.v = v;
        }
    }

    // Returns the computed value of a String constant at the earliest point possible
    // (e.g. if two Strings are concatenated, at the start of the concatenation,
    // if a String is constructed from a byte array, at the point where NEW java/lang/String is called).
    // This is to support figuring out where a previous stack value is (for String concatenations ect).
    // If the String is null, it was unable to be computed at the given point,
    // either because it can't be determined, or my code doesn't support figuring it out yet.
    private static Pair<AbstractInsnNode, String> tryComputeString(AbstractInsnNode stringOnStack) {
        if (stringOnStack == null) {
            return new Pair<>(stringOnStack, null);
        }

        final int opcode = stringOnStack.getOpcode();

        if (opcode == Opcodes.LDC) {
            final LdcInsnNode ldc = (LdcInsnNode) stringOnStack;

            if (ldc.cst instanceof String) {
                return new Pair<>(stringOnStack, (String) ldc.cst);
            }
        }

        // TODO Real analysis
        if (isOpcodeMethodInvoke(opcode)) {
            final MethodInsnNode methodInsNode = (MethodInsnNode) stringOnStack;
            final String methodOwner = methodInsNode.owner;
            final String methodName = methodInsNode.name;
            final String methodDesc = methodInsNode.desc;

            // TODO Support more methods
            if ((opcode == Opcodes.INVOKEVIRTUAL)
                    && "java/lang/String".equals(methodOwner)
                    && "concat".equals(methodName)
                    && "(Ljava/lang/String;)Ljava/lang/String;".equals(methodDesc)) {
                final Pair<AbstractInsnNode, String> firstPair = tryComputeString(stringOnStack.getPrevious());

                if ((firstPair.v != null) && (firstPair.k != null)) {
                    final Pair<AbstractInsnNode, String> secondPair = tryComputeString(firstPair.k.getPrevious());

                    if (secondPair.v != null) {
                        return new Pair<>(secondPair.k, secondPair.v + firstPair.v);
                    }
                }
            }

            if ((opcode == Opcodes.INVOKESPECIAL)
                    && "java/lang/String".equals(methodOwner)
                    && "<init>".equals(methodName)
                    && "([B)V".equals(methodDesc)) {
                // This matches the pattern that javac uses to construct Strings from code like
                // new String(new byte[] { some, bytes, ect });
                // The code is Not Good
                final Map<Integer, Byte> indexToByte = new HashMap<>();
                AbstractInsnNode storeNextByte = stringOnStack.getPrevious();

                while ((storeNextByte != null) && (storeNextByte.getOpcode() == Opcodes.BASTORE)) {
                    final AbstractInsnNode valueIns = storeNextByte.getPrevious();

                    if (valueIns == null) {
                        break;
                    }

                    final Number value = getValueOrNull(valueIns);

                    if (value == null) {
                        break;
                    }

                    final AbstractInsnNode indexIns = valueIns.getPrevious();

                    if (indexIns == null) {
                        break;
                    }

                    final Number index = getValueOrNull(indexIns);

                    if (index == null) {
                        break;
                    }

                    final AbstractInsnNode dup = indexIns.getPrevious();

                    if ((dup == null) || (dup.getOpcode() != Opcodes.DUP)) {
                        break;
                    }

                    indexToByte.putIfAbsent(index.intValue(), value.byteValue());
                    storeNextByte = dup.getPrevious();
                }

                if (storeNextByte != null) {
                    final AbstractInsnNode newArrayIns = storeNextByte;

                    if ((newArrayIns != null) && (newArrayIns.getOpcode() == Opcodes.NEWARRAY)) {
                        final IntInsnNode newArray = (IntInsnNode) newArrayIns;

                        if (newArray.operand == Opcodes.T_BYTE) {
                            final AbstractInsnNode newArrayLengthIns = newArrayIns.getPrevious();

                            if (newArrayLengthIns != null) {
                                final Number value = getValueOrNull(newArrayLengthIns);

                                if (value != null) {
                                    final AbstractInsnNode firstDup = newArrayLengthIns.getPrevious();

                                    if ((firstDup != null) && (firstDup.getOpcode() == Opcodes.DUP)) {
                                        final AbstractInsnNode firstNewIns = firstDup.getPrevious();

                                        if ((firstNewIns != null) && (firstNewIns.getOpcode() == Opcodes.NEW)) {
                                            final TypeInsnNode firstNew = (TypeInsnNode) firstNewIns;

                                            if ("java/lang/String".equals(firstNew.desc)) {
                                                final byte[] stringBytes = new byte[value.intValue()];
                                                indexToByte.forEach((k, v) -> {
                                                    if (k < stringBytes.length) {
                                                        stringBytes[k]
                                                        = v;
                                                    } else {
                                                        System.err.println("grievous error: index out of bounds when constructing String from fixed byte array, index " + k + " value " + v);
                                                    }
                                                });
                                                final String str = new String(stringBytes);
                                                return new Pair<>(firstNew, str);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return new Pair<>(stringOnStack, null);
    }

    public static String tryComputeConstantString(AbstractInsnNode stringOnStack) {
        return tryComputeString(stringOnStack).v;
    }
}

package com.github.NeRdTheNed.jSus.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
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

import me.coley.cafedude.classfile.ClassFile;
import me.coley.cafedude.io.ClassFileReader;
import me.coley.cafedude.io.ClassFileWriter;
import me.coley.cafedude.transform.IllegalStrippingTransformer;

public final class Util {
    /** Private constructor to hide the default one */
    private Util() {
        // This space left intentionally blank
    }

    public static final Base64.Decoder decoder = Base64.getDecoder();

    public static byte[] convertInputStreamToBytes(InputStream in) throws IOException {
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        final byte[] buffer = new byte[1024];
        int length;

        while ((length = in.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }

        return result.toByteArray();
    }

    public static ClassNode streamToClass(InputStream stream, String name) throws IOException {
        final byte[] classBytes = convertInputStreamToBytes(stream);
        return bytesToClass(classBytes, name);
    }

    public static ClassNode bytesToClass(byte[] clazz, String name) {
        try {
            final ClassReader reader = new ClassReader(clazz);
            final ClassNode node = new ClassNode();
            reader.accept(node, ClassReader.SKIP_DEBUG);
            return node;
        } catch (final Exception e) {
            System.err.println("Malformed class " + name + ", trying to read with CAFED00D");
            e.printStackTrace();
        }

        try {
            final ClassFileReader classFileReader = new ClassFileReader();
            final ClassFile classFile = classFileReader.read(clazz);
            // Try to remove junk data that confuses ASM
            new IllegalStrippingTransformer(classFile).transform();
            final byte[] fixedClass = new ClassFileWriter().write(classFile);
            final ClassReader reader = new ClassReader(fixedClass);
            final ClassNode node = new ClassNode();
            reader.accept(node, ClassReader.SKIP_DEBUG);
            return node;
        } catch (final Exception e) {
            System.err.println("Malformed class " + name + ", could not read with CAFED00D");
            e.printStackTrace();
        }

        return null;
    }

    public static ClassNode classfileFileToClass(File clazz) {
        return classfilePathToClass(clazz.toPath());
    }

    public static ClassNode classfilePathToClass(Path clazz) {
        try
            (final InputStream is = Files.newInputStream(clazz)) {
            return streamToClass(is, clazz.toString());
        } catch (final IOException e) {
            System.err.println("Error reading class file " + clazz);
            e.printStackTrace();
        }

        return null;
    }

    private static void findAddNodes(JarFile jarFile, List<? super ClassNode> nodes, boolean verbose, boolean noLog) {
        String obf = null;
        boolean didFindWeirdObf = false;
        final Enumeration<JarEntry> entries = jarFile.entries();

        while (entries.hasMoreElements()) {
            final JarEntry entry = entries.nextElement();
            final String name = entry.getName();
            final boolean weirdClassObf = name.endsWith(".class/") && ((entry.getCompressedSize() > 0L) || (entry.getSize() > 0L));

            if (entry.isDirectory() && !weirdClassObf) {
                continue;
            }

            if (weirdClassObf) {
                didFindWeirdObf = true;
            }

            if (name.endsWith(".jar")) {
                if (verbose && !noLog) {
                    System.out.println("- Adding JIJ " + name + " to scan");
                }

                Path tempFile = null;

                try
                    (final InputStream is = jarFile.getInputStream(entry)) {
                    // TODO Not totally sure if this is correct
                    tempFile = Files.createTempFile("jSus-temp-", ".jar");
                    tempFile.toFile().deleteOnExit();
                    Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);

                    try
                        (final JarFile jij = new JarFile(tempFile.toFile())) {
                        findAddNodes(jij, nodes, verbose, noLog);
                    }
                } catch (final Exception e) {
                    System.err.println("Issue extracting JIJ " + name + " from " + jarFile.getName() + " to temporary file");
                    e.printStackTrace();
                } finally {
                    if (tempFile != null) {
                        try {
                            Files.deleteIfExists(tempFile);
                        } catch (final Exception e) {
                            System.err.println("Issue deleting temporary file " + tempFile);
                            e.printStackTrace();
                        }
                    }
                }
            }

            if (weirdClassObf || name.endsWith(".class")) {
                try
                    (final InputStream is = jarFile.getInputStream(entry)) {
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

        if ((obf != null) && !noLog) {
            System.out.println("- Note: jar " + jarFile.getName() + " claims to be obfuscated by " + obf);
        }

        if (didFindWeirdObf && !noLog) {
            System.out.println("- Note: Common class obfuscation technique was used in jar " + jarFile.getName());
        }
    }

    public static List<ClassNode> gatherClassNodesFromJar(JarFile jarFile, boolean verbose, boolean noLog) {
        final List<ClassNode> nodes = new ArrayList<>();
        findAddNodes(jarFile, nodes, verbose, noLog);
        return nodes;
    }

    public static String opcodeName(int opcode) {
        return (opcode >= 0) && (opcode < Printer.OPCODES.length) ? Printer.OPCODES[opcode] : Integer.toString(opcode);
    }

    public static boolean isOpcodeMethodInvoke(int opcode) {
        return (opcode <= Opcodes.INVOKEINTERFACE) && (opcode >= Opcodes.INVOKEVIRTUAL);
    }

    public static boolean isCommonBase64DecodeMethod(int opcode, String owner, String name, String signature) {
        if (!"(Ljava/lang/String;)[B".equals(signature)) {
            return false;
        }

        // TODO handle org.apache.commons.codec.binary.Base16
        // TODO handle org.apache.commons.codec.binary.Base32
        // TODO handle org.apache.commons.codec.binary.Hex
        return ("java/util/Base64$Decoder".equals(owner) && "decode".equals(name)) ||
               ("org/apache/commons/codec/binary/Base64".equals(owner) && "decodeBase64".equals(name)) ||
               ("javax/xml/bind/DatatypeConverter".equals(owner) && "parseBase64Binary".equals(name));
    }

    public static boolean isCommonBase64DecodeBytesToBytesMethod(int opcode, String owner, String name, String signature) {
        if (!"([B)[B".equals(signature)) {
            return false;
        }

        // TODO Support more methods
        return ("java/util/Base64$Decoder".equals(owner) && "decode".equals(name)) ||
               ("org/apache/commons/codec/binary/Base64".equals(owner) && "decodeBase64".equals(name));
    }

    private static Number getValueOrNull(AbstractInsnNode load) {
        switch (load.getOpcode()) {
        case Opcodes.ICONST_M1:
            return -1;

        case Opcodes.ICONST_0:
            return 0;

        case Opcodes.ICONST_1:
            return 1;

        case Opcodes.ICONST_2:
            return 2;

        case Opcodes.ICONST_3:
            return 3;

        case Opcodes.ICONST_4:
            return 4;

        case Opcodes.ICONST_5:
            return 5;

        case Opcodes.LCONST_0:
            return 0L;

        case Opcodes.LCONST_1:
            return 1L;

        case Opcodes.FCONST_0:
            return 0.0f;

        case Opcodes.FCONST_1:
            return 1.0f;

        case Opcodes.FCONST_2:
            return 2.0f;

        case Opcodes.DCONST_0:
            return 0.0d;

        case Opcodes.DCONST_1:
            return 1.0d;

        case Opcodes.BIPUSH:
        case Opcodes.SIPUSH:
            return ((IntInsnNode) load).operand;

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

    private static Pair<AbstractInsnNode, byte[]> getArrayConstructPoint(AbstractInsnNode postInit, Map<Integer, Byte> indexToByte) {
        if (postInit != null) {
            final AbstractInsnNode newArrayIns = postInit.getPrevious();

            if ((newArrayIns != null) && (newArrayIns.getOpcode() == Opcodes.NEWARRAY)) {
                final IntInsnNode newArray = (IntInsnNode) newArrayIns;

                if (newArray.operand == Opcodes.T_BYTE) {
                    final AbstractInsnNode newArrayLengthIns = newArrayIns.getPrevious();

                    if (newArrayLengthIns != null) {
                        final Number value = getValueOrNull(newArrayLengthIns);

                        if (value != null) {
                            final byte[] computedBytes = new byte[value.intValue()];
                            indexToByte.forEach((k, v) -> {
                                if (k < computedBytes.length) {
                                    computedBytes[k]
                                    = v;
                                } else {
                                    System.err.println("grievous error: index out of bounds when reconstructing fixed byte array, index " + k + " value " + v);
                                }
                            });
                            return new Pair<>(newArrayLengthIns, computedBytes);
                        }
                    }
                }
            }
        }

        return new Pair<>(null, null);
    }

    private static Pair<AbstractInsnNode, byte[]> tryComputeArray(AbstractInsnNode arrayOnStack) {
        if (arrayOnStack == null) {
            return new Pair<>(null, null);
        }

        final int opcode = arrayOnStack.getOpcode();

        if (opcode == Opcodes.BASTORE) {
            // This matches the pattern that javac uses to construct Strings from code like
            // new String(new byte[] { some, bytes, ect });
            // The code is Not Good
            final Map<Integer, Byte> indexToByte = new HashMap<>();
            AbstractInsnNode storeNextByte = arrayOnStack;

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
                return getArrayConstructPoint(storeNextByte.getNext(), indexToByte);
            }
        } else if (isOpcodeMethodInvoke(opcode)) {
            final MethodInsnNode methodInsNode = (MethodInsnNode) arrayOnStack;
            final String methodOwner = methodInsNode.owner;
            final String methodName = methodInsNode.name;
            final String methodDesc = methodInsNode.desc;

            if (isCommonBase64DecodeBytesToBytesMethod(opcode, methodOwner, methodName, methodDesc)) {
                final Pair<AbstractInsnNode, byte[]> computedArray = tryComputeArray(arrayOnStack.getPrevious());

                if (computedArray.v != null) {
                    AbstractInsnNode postInit = null;

                    if (opcode == Opcodes.INVOKESTATIC) {
                        postInit = computedArray.k;
                    } else if (computedArray.k != null) {
                        final AbstractInsnNode prev = computedArray.k.getPrevious();

                        if (prev != null) {
                            final int prevOpcode = prev.getOpcode();

                            if (isOpcodeMethodInvoke(prevOpcode)) {
                                final MethodInsnNode prevMethodInsNode = (MethodInsnNode) prev;
                                final String prevMethodOwner = prevMethodInsNode.owner;
                                final String prevMethodName = prevMethodInsNode.name;
                                final String prevMethodDesc = prevMethodInsNode.desc;

                                if ((prevOpcode == Opcodes.INVOKESTATIC)
                                        && "java/util/Base64".equals(prevMethodOwner)
                                        && "getDecoder".equals(prevMethodName)
                                        && "()Ljava/util/Base64$Decoder;".equals(prevMethodDesc)) {
                                    postInit = prev;
                                }
                            } else if (prevOpcode == Opcodes.ALOAD) {
                                postInit = prev;
                            }
                        }
                    }

                    try {
                        final byte[] decoded = decoder.decode(computedArray.v);
                        return new Pair<>(postInit, decoded);
                    } catch (final IllegalArgumentException e) {
                        // Invalid Base64?
                    }
                }
            }
        }

        return new Pair<>(arrayOnStack, null);
    }

    public static byte[] tryComputeConstantBytes(AbstractInsnNode stringOnStack) {
        return tryComputeArray(stringOnStack).v;
    }

    private static Pair<AbstractInsnNode, String> getStringConstructPoint(AbstractInsnNode postInit, String str) {
        if (postInit != null) {
            final AbstractInsnNode firstDup = postInit.getPrevious();

            if ((firstDup != null) && (firstDup.getOpcode() == Opcodes.DUP)) {
                final AbstractInsnNode firstNewIns = firstDup.getPrevious();

                if ((firstNewIns != null) && (firstNewIns.getOpcode() == Opcodes.NEW)) {
                    final TypeInsnNode firstNew = (TypeInsnNode) firstNewIns;

                    if ("java/lang/String".equals(firstNew.desc)) {
                        return new Pair<>(firstNew, str);
                    }
                }
            }
        }

        return new Pair<>(null, str);
    }

    // Returns the computed value of a String constant at the earliest point possible
    // (e.g. if two Strings are concatenated, at the start of the concatenation,
    // if a String is constructed from a byte array, at the point where NEW java/lang/String is called).
    // This is to support figuring out where a previous stack value is (for String concatenations ect).
    // If the String is null, it was unable to be computed at the given point,
    // either because it can't be determined, or my code doesn't support figuring it out yet.
    private static Pair<AbstractInsnNode, String> tryComputeString(AbstractInsnNode stringOnStack) {
        if (stringOnStack == null) {
            return new Pair<>(null, null);
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
                final AbstractInsnNode prev = stringOnStack.getPrevious();

                if (prev != null) {
                    final int prevOpcode = prev.getOpcode();

                    if (isOpcodeMethodInvoke(prevOpcode)) {
                        final MethodInsnNode possibleBase64 = (MethodInsnNode) prev;
                        final String prevMethodOwner = possibleBase64.owner;
                        final String prevMethodName = possibleBase64.name;
                        final String prevMethodDesc = possibleBase64.desc;

                        if (isCommonBase64DecodeMethod(prevOpcode, prevMethodOwner, prevMethodName, prevMethodDesc)) {
                            final Pair<AbstractInsnNode, String> passedBase64 = tryComputeString(prev.getPrevious());

                            if (passedBase64.v != null) {
                                AbstractInsnNode postInit = null;

                                if (prevOpcode == Opcodes.INVOKESTATIC) {
                                    postInit = passedBase64.k;
                                } else if (passedBase64.k != null) {
                                    final AbstractInsnNode preBase64 = passedBase64.k.getPrevious();

                                    if (preBase64 != null) {
                                        final int preBase64Opcode = preBase64.getOpcode();

                                        if (isOpcodeMethodInvoke(preBase64Opcode)) {
                                            final MethodInsnNode prevMethodInsNode = (MethodInsnNode) preBase64;
                                            final String preBase64MethodOwner = prevMethodInsNode.owner;
                                            final String preBase64MethodName = prevMethodInsNode.name;
                                            final String preBase64MethodDesc = prevMethodInsNode.desc;

                                            if ((preBase64Opcode == Opcodes.INVOKESTATIC)
                                                    && "java/util/Base64".equals(preBase64MethodOwner)
                                                    && "getDecoder".equals(preBase64MethodName)
                                                    && "()Ljava/util/Base64$Decoder;".equals(preBase64MethodDesc)) {
                                                postInit = preBase64;
                                            }
                                        } else if (preBase64Opcode == Opcodes.ALOAD) {
                                            postInit = preBase64;
                                        }
                                    }
                                }

                                final String possibleString = passedBase64.v;

                                try {
                                    final String decoded = new String(decoder.decode(possibleString));
                                    return getStringConstructPoint(postInit, decoded);
                                } catch (final IllegalArgumentException e) {
                                    // Invalid Base64?
                                }
                            }
                        }
                    }

                    final Pair<AbstractInsnNode, byte[]> computedArray = tryComputeArray(prev);

                    if (computedArray.v != null) {
                        final String str = new String(computedArray.v);
                        return getStringConstructPoint(computedArray.k, str);
                    }
                }
            }

            if ((opcode == Opcodes.INVOKESPECIAL)
                    && "java/lang/String".equals(methodOwner)
                    && "<init>".equals(methodName)
                    && "(Ljava/lang/String;)V".equals(methodDesc)) {
                final Pair<AbstractInsnNode, String> prevString = tryComputeString(stringOnStack.getPrevious());
                return getStringConstructPoint(prevString.k, prevString.v);
            }
        }

        return new Pair<>(stringOnStack, null);
    }

    public static String tryComputeConstantString(AbstractInsnNode stringOnStack) {
        return tryComputeString(stringOnStack).v;
    }
}

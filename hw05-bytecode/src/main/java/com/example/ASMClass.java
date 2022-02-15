package com.example;

import lombok.Getter;
import lombok.Setter;
import org.objectweb.asm.*;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Класс для создания прокси на заданные методы
 * */
@Getter
@Setter
public class ASMClass {
    private static final String PROXY_PREFIX = "proxied";
    private static final int CURRENT_ASM_API_VERSION = Opcodes.ASM9;
    private Collection<Method> proxiedMethods;

    private final ClassReader classReader;
    private final ClassWriter classWriter;
    private final Handle stringConcatMethodHandle;
    private final ClassVisitor classVisitor;

    public ASMClass(byte[] classBytes, Collection<Method> proxiedMethods) {
        this.proxiedMethods = proxiedMethods == null ? new ArrayList<>() : proxiedMethods;

        this.classReader = new ClassReader(classBytes);
        this.classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);

        this.stringConcatMethodHandle = new Handle(
                Opcodes.H_INVOKESTATIC,
                Type.getInternalName(java.lang.invoke.StringConcatFactory.class),
                "makeConcatWithConstants",
                MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, String.class, Object[].class).toMethodDescriptorString(),
                false);

        this.classVisitor = new ClassVisitor(CURRENT_ASM_API_VERSION, classWriter) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                if (isMethodInProxied(name, descriptor)) {
                    return super.visitMethod(access, getProxiedMethodName(name), descriptor, signature, exceptions);
                } else {
                    return super.visitMethod(access, name, descriptor, signature, exceptions);
                }
            }
        };
    }

    /**
     * Запуск создания прокси для методов
     * */
    public void createProxies() {
        classReader.accept(classVisitor, CURRENT_ASM_API_VERSION);
        proxiedMethods.forEach(this::setProxyForMethod);
    }

    /**
     * Отдача результата изменения класса
     * */
    public byte[] getClassBytes() {
        return this.classWriter.toByteArray();
    }

    /**
     * Имеется ли метод с такими именем и дескриптором среди методов, которым нужно создать прокси метод
     * @param name Имя метода для поиска
     * @param descriptor Дескриптор метода для поиска
     * */
    private boolean isMethodInProxied(String name, String descriptor) {
        return this.proxiedMethods.stream()
                .anyMatch(item -> name.equals(item.getName()) && descriptor.equals(Type.getMethodDescriptor(item)));
    }

    /**
     * Формирование имени проксирующего метода, на основе имени оригинального метода
     * @param originalMethodName Имя оригинального метода
     * */
    private String getProxiedMethodName(String originalMethodName) {
        return PROXY_PREFIX + originalMethodName;
    }

    /**
     * Создание проксирующего метода
     * @param method Метод, для которого создается прокси
     * */
    private void setProxyForMethod(Method method) {
        final boolean isMethodStatic = (method.getModifiers() & Opcodes.ACC_STATIC) > 0;
        final String methodDescriptor = Type.getMethodDescriptor(method);

        // Создание метода для вывода описания вызванного метода
        MethodVisitor methodVisitor = classWriter.visitMethod(method.getModifiers(), method.getName(), Type.getMethodDescriptor(method), null, null);

        printString(methodVisitor, this.stringConcatMethodHandle, "Executed method: " + method.getName() + ", params: ");

        if (!isMethodStatic) { // Передача this если не статичный метод
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        }

        int argNum = isMethodStatic ? 0 : 1;
        Type[] args = Type.getArgumentTypes(method);
        for (int argIndex = 0; argIndex < args.length; argIndex++) {
            Type type = args[argIndex];

            methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            methodVisitor.visitVarInsn(type.getOpcode(Opcodes.ILOAD), argNum);
            methodVisitor.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "java/io/PrintStream",
                    "print",
                    String.format("(%s)V", type.getDescriptor()),
                    false
            );

            argNum += type.getSize();

            if (argIndex < args.length - 1) {
                printString(methodVisitor, this.stringConcatMethodHandle, ", ");
            } else {
                printString(methodVisitor, this.stringConcatMethodHandle, "\n");
            }
        }

        // Вызов проксируемого метода
        argNum = isMethodStatic ? 0 : 1;
        for (Type type: args) {
            methodVisitor.visitVarInsn(type.getOpcode(Opcodes.ILOAD), argNum);
            argNum += type.getSize();
        }

        methodVisitor.visitMethodInsn(
                isMethodStatic ? Opcodes.INVOKESTATIC: Opcodes.INVOKEVIRTUAL,
                method.getDeclaringClass().getName().replace(".", "/"),
                getProxiedMethodName(method.getName()),
                methodDescriptor, false
        );

        methodVisitor.visitInsn(Opcodes.RETURN);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();
    }

    /**
     * Печать статичной строки
     * @param methodVisitor Визитор на класс
     * @param methodHandle Дескриптор на метод соединения строк
     * */
    private void printString(MethodVisitor methodVisitor, Handle methodHandle, String value) {
        methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        methodVisitor.visitLdcInsn(value);
        methodVisitor.visitInvokeDynamicInsn("makeConcatWithConstants", "(Ljava/lang/String;)Ljava/lang/String;", methodHandle, "\u0001");
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "print", "(Ljava/lang/String;)V", false);
    }
}

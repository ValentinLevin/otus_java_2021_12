package com.example;

import com.example.utils.IOUtils;
import org.objectweb.asm.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Класс для создания прокси на методы, отмеченные выбранными аннотациями
 * */
public class ASMClass {
    private static final String PROXY_PREFIX = "proxied";
    private static final int CURRENT_ASM_API_VERSION = Opcodes.ASM9;

    private final String classFileName;
    private final Collection<Class<? extends Annotation>> annotationsForProxying;

    private Class<?> clazz;
    private Collection<Method> annotatedMethods;
    private boolean classIsProcessed;
    private ClassReader classReader;
    private ClassWriter classWriter;
    private final Handle stringConcatMethodHandle;

    public ASMClass(String classFileName) {
        this(classFileName, (Class<? extends Annotation>) null);
    }

    @SafeVarargs
    public ASMClass(String classFileName, Class<? extends Annotation>... annotationsForProxying) {
        this.classFileName = classFileName;
        this.classIsProcessed = false;
        this.annotationsForProxying = annotationsForProxying == null ? new ArrayList<>() : Arrays.asList(annotationsForProxying);

        this.stringConcatMethodHandle = new Handle(
                Opcodes.H_INVOKESTATIC,
                Type.getInternalName(java.lang.invoke.StringConcatFactory.class),
                "makeConcatWithConstants",
                MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, String.class, Object[].class).toMethodDescriptorString(),
                false);
    }

    /**
     * Метод для обработки класса в одну команду
     * */
    @SafeVarargs
    public static byte[] createProxyForAnnotations(String classFileName, Class<? extends Annotation>... annotations) throws IOException, ClassNotFoundException {
        ASMClass instance = new ASMClass(classFileName, annotations);
        instance.createProxies();
        return instance.getClassBytes();
    }

    /**
     * Добавление аннотации в список, для методов которых нужно создать прокси
     * */
    public void addAnnotationForProxying(Class<? extends Annotation> annotation) {
        if (this.classIsProcessed) {
            throw new RuntimeException("Class is already processed");
        }
        this.annotationsForProxying.add(annotation);
    }

    /**
     * Удаление аннотации из списка, для методов которых нужно создать прокси
     * */
    public void removeAnnotationForProxying(Class<? extends Annotation> annotation) {
        if (classIsProcessed) {
            throw new RuntimeException("Class is already processed");
        }

        this.annotationsForProxying.remove(annotation);
    }

    /**
     * Запуск создания прокси для методов
     * */
    public void createProxies() throws IOException, ClassNotFoundException {
        loadClassFromSystemClassLoader();
        findAnnotatedMethods();

        ClassVisitor classVisitor = new ClassVisitor(CURRENT_ASM_API_VERSION, this.classWriter) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                if (isMethodAnnotated(name, descriptor)) {
                    return super.visitMethod(access, getProxiedMethodName(name), descriptor, signature, exceptions);
                } else {
                    return super.visitMethod(access, name, descriptor, signature, exceptions);
                }
            }
        };

        classReader.accept(classVisitor, CURRENT_ASM_API_VERSION);
        annotatedMethods.forEach(this::createProxyForMethod);

        this.classIsProcessed = true;
    }

    /**
     * Загрузка класса из файла
     * */
    private void loadClassFromSystemClassLoader() throws ClassNotFoundException, IOException {
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(classFileName);
        if (is != null) {
            String className = classFileName.replace(".class", "").replace("/", ".");
            this.clazz = Class.forName(className, false, ClassLoader.getSystemClassLoader());
        } else {
            throw new RuntimeException(String.format("Not found class file with name %s", this.classFileName));
        }

        this.classReader = new ClassReader(IOUtils.getBytesFromInputStream(is));
        this.classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
    }

    /**
     * Поиск аннотированных заданными аннотациями методов
     * */
    private void findAnnotatedMethods() {
        this.annotatedMethods =
                Arrays.stream(clazz.getMethods())
                    .filter(method ->
                            Arrays.stream(method.getAnnotations()).anyMatch(item -> this.annotationsForProxying.contains(item.getClass()))
                    )
                    .toList();
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
    private boolean isMethodAnnotated(String name, String descriptor) {
        return this.annotatedMethods.stream()
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
    private void createProxyForMethod(Method method) {
        final boolean isMethodStatic = (method.getModifiers() & Opcodes.ACC_STATIC) > 0;
        final String methodDescriptor = Type.getMethodDescriptor(method);

        // Создание метода для вывода описания вызванного метода
        MethodVisitor methodVisitor = classWriter.visitMethod(method.getModifiers(), method.getName(), Type.getMethodDescriptor(method), null, null);

        addPrintStringInstruction(methodVisitor, this.stringConcatMethodHandle, "Executed method: " + method.getName() + ", params: ");

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
                addPrintStringInstruction(methodVisitor, this.stringConcatMethodHandle, ", ");
            } else {
                addPrintStringInstruction(methodVisitor, this.stringConcatMethodHandle, "\n");
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
     * Добавление инструкции печати статичной строки
     * @param methodVisitor Визитор на класс
     * @param methodHandle Дескриптор на метод соединения строк
     * */
    private void addPrintStringInstruction(MethodVisitor methodVisitor, Handle methodHandle, String value) {
        methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        methodVisitor.visitLdcInsn(value);
        methodVisitor.visitInvokeDynamicInsn("makeConcatWithConstants", "(Ljava/lang/String;)Ljava/lang/String;", methodHandle, "\u0001");
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "print", "(Ljava/lang/String;)V", false);
    }
}

package com.example;

import com.example.annotations.Log;
import com.example.utils.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;

public class App {
    public static void main(String[] args) throws IOException, URISyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (!Arrays.asList(args).contains("isChanged")) {
            App.prepare();
        } else {
            App.action();
        }
    }

    /**
     * Запуск реальной работы приложения
     * */
    private static void action() {
        TestLogging testLogging = new TestLogging();

        System.out.println("-----------------------------------");
        testLogging.sqrt(2);

        System.out.println("-----------------------------------");
        testLogging.sum(3,4);

        System.out.println("-----------------------------------");
        testLogging.sum(5,6,7);

        System.out.println("-----------------------------------");
        testLogging.sub(8,9);

        System.out.println("-----------------------------------");
        testLogging.sqrt(10, "test");

        System.out.println("-----------------------------------");
        TestLogging.staticsqrt(3);

        System.out.println("-----------------------------------");
    }

    /**
     * Подготовка приложения для работы (на данный момент только поиск и обработка аннотированных методов )
     * */
    private static void prepare() throws ClassNotFoundException, IOException, URISyntaxException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> mainClass = FileUtils.getMainClass();
        Collection<String> classFileList =
                FileUtils.getClassNamesFromPackage(ClassLoader.getSystemClassLoader(), mainClass.getPackageName());
        MyClassLoader loader = new MyClassLoader();
        for (String classFile: classFileList) {
            createProxyForAnnotatedMethods(loader, classFile, Log.class);
        }

        Class<?> clazz = Class.forName(mainClass.getName(), true, loader);
        Object object = clazz.getConstructor().newInstance();

        Method method = clazz.getMethod("main", String[].class);
        method.invoke(object, (Object) new String[]{"isChanged"});
    }

    /**
     * Поиск и обработка аннотированных методов
     * @param classLoader загрузчик, в который будет загружаться обработанный класс
     * @param classFileName имя класса для обработки
     * @param annotationClass аннотация, с которой методы будут обработаны
     * */
    private static void createProxyForAnnotatedMethods(MyClassLoader classLoader, String classFileName,
                                                       Class<? extends java.lang.annotation.Annotation> annotationClass) throws IOException, ClassNotFoundException
    {
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(classFileName);
        if (is != null) {
            String className = classFileName.replace(".class", "").replace("/", ".");
            Class<?> clazz = Class.forName(className, false, ClassLoader.getSystemClassLoader());

            Collection<Method> annotatedMethods = Arrays.stream(clazz.getMethods())
                    .filter(method -> method.isAnnotationPresent(annotationClass))
                    .toList();

            byte[] classBytes;
            if (!annotatedMethods.isEmpty()) {
                ASMClass asmClass = new ASMClass(getBytesFromInputStream(is), annotatedMethods);
                asmClass.createProxies();
                classBytes = asmClass.getClassBytes();
            } else {
                classBytes = getBytesFromInputStream(is);
            }

            classLoader.defineClass(classBytes, className);
        }
    }

    /**
     * Получение массива байтов из потока
     * @param inputStream поток для обработки
     * */
    private static byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream bs = new ByteArrayOutputStream(inputStream.available());

        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            bs.write(data, 0, nRead);
        }

        return bs.toByteArray();
    }
}

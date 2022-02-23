package com.example;

import com.example.annotations.Log;
import com.example.utils.FileUtils;

import java.io.IOException;
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

        MyClassLoader myClassLoader = new MyClassLoader();
        for (String classFile: classFileList) {
            byte[] changedClassBytes = ASMClass.createProxyForAnnotations(classFile, Log.class);
            myClassLoader.defineClass(changedClassBytes, classFile.replace(".class", "").replace("/", "."));
        }

        Class<?> clazz = Class.forName(mainClass.getName(), true, myClassLoader);
        Object object = clazz.getConstructor().newInstance();

        Method method = clazz.getMethod("main", String[].class);
        method.invoke(object, (Object) new String[]{"isChanged"});
    }
}

package com.example;

public class MyClassLoader extends ClassLoader {
    public MyClassLoader() {
        super("myClassLoader", getSystemClassLoader());
    }

    public Class<?> defineClass(byte[] bytes, String className) {
        return super.defineClass(className, bytes, 0, bytes.length);
    }
}

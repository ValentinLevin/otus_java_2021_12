package com.example.helper;

import com.example.exception.MethodInvokeException;
import com.example.exception.ObjectInstantiateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;


public class ReflectionHelper {
    private static final Logger log = LoggerFactory.getLogger(ReflectionHelper.class);

    private ReflectionHelper() {
    }

    public static <C> C callMethod(Method method, Object object, Object... args) {
        try {
            return (C) method.invoke(object, args);
        } catch (Exception e) {
            log.error("Error on method invoke: {}", method.getName(), e);
            throw new MethodInvokeException(method, object);
        }
    }

    public static List<Method> getAnnotatedMethods(Class<? extends Annotation> annotationClass, Class<?> configClass) {
        return Arrays.stream(configClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(annotationClass))
                .toList();
    }

    public static <T> T instantiate(Class<T> type, Object... args) {
        try {
            if (args.length == 0) {
                return type.getDeclaredConstructor().newInstance();
            } else {
                Class<?>[] classes = toClasses(args);
                return type.getDeclaredConstructor(classes).newInstance(args);
            }
        } catch (Exception e) {
            log.error("Error on class instance creation: {}", type.getCanonicalName(), e);
            throw new ObjectInstantiateException(type);
        }
    }

    public static Class<?>[] toClasses(Object[] args) {
        return Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new);
    }
}

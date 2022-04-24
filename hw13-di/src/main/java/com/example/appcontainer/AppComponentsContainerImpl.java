package com.example.appcontainer;

import com.example.appcontainer.api.AppComponent;
import com.example.appcontainer.api.AppComponentsContainer;
import com.example.appcontainer.api.AppComponentsContainerConfig;
import com.example.exception.CircularDependencyException;
import com.example.exception.NotFoundComponentException;
import com.example.exception.TooManyComponentException;
import com.example.helper.ReflectionHelper;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Method;
import java.util.*;

public class AppComponentsContainerImpl implements AppComponentsContainer {
    private final Map<String, AppComponentInfo> appComponentsByName = new HashMap<>();
    private final Map<Class<?>, AppComponentInfo> appComponentsByClass = new HashMap<>();

    public AppComponentsContainerImpl(Class<?>...initialConfigClasses) {
        processConfig(initialConfigClasses);
    }

    public AppComponentsContainerImpl(String packageName) {
        Reflections reflections = new Reflections(packageName, Scanners.TypesAnnotated);
        Set<Class<?>> initialConfigClasses = reflections.getTypesAnnotatedWith(AppComponentsContainerConfig.class);
        processConfig(initialConfigClasses.toArray(new Class<?>[0]));
    }

    private void processConfig(Class<?>...configClasses) {
        for (Class<?> configClass: configClasses) {
            checkConfigClass(configClass);
            readAnnotatedMethods(configClass);
        }
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    private void readAnnotatedMethods(Class<?> configClass) {
        Object configClassObject = null;
        for (Method method: ReflectionHelper.getAnnotatedMethods(AppComponent.class, configClass)) {
            Class<?> componentClass = method.getReturnType();
            if (!appComponentsByClass.containsKey(componentClass)) {
                if (configClassObject == null) {
                    configClassObject = ReflectionHelper.instantiate(configClass);
                }

                AppComponent appComponentAnnotation = method.getAnnotation(AppComponent.class);
                String componentName = appComponentAnnotation.name();
                AppComponentInfo appComponentInfo = new AppComponentInfo(method, componentClass, componentName, configClassObject);

                appComponentsByClass.put(componentClass, appComponentInfo);
                appComponentsByName.put(componentName, appComponentInfo);
                appComponentsByName.put(componentClass.getSimpleName(), appComponentInfo);
                appComponentsByName.put(componentClass.getCanonicalName(), appComponentInfo);
                for (Class<?> classInterface: componentClass.getInterfaces()) {
                    appComponentsByName.put(classInterface.getSimpleName(), appComponentInfo);
                    appComponentsByName.put(classInterface.getCanonicalName(), appComponentInfo);
                }
            }
        }
    }

    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        List<Class<?>> assignableComponentClasses =
                appComponentsByClass.keySet().stream()
                        .filter(item -> item.isAssignableFrom(componentClass))
                        .toList();

        if (assignableComponentClasses.isEmpty()) {
            throw new NotFoundComponentException(componentClass);
        }

        if (assignableComponentClasses.size() > 1) {
            throw new TooManyComponentException(componentClass);
        }

        Class<?> appComponentClass = assignableComponentClasses.get(0);

        C appComponent = appComponentsByClass.get(appComponentClass).getAppComponent();
        if (appComponent == null) {
            appComponent = loadComponent(appComponentClass, new ArrayList<>());
        }

        return appComponent;
    }

    @Override
    public <C> C getAppComponent(String componentName) {
        AppComponentInfo appComponentInfo = appComponentsByName.get(componentName);
        if (appComponentInfo.getAppComponent() == null) {
            return loadComponent(appComponentInfo.componentClass, new ArrayList<>());
        } else {
            return appComponentInfo.getAppComponent();
        }
    }

    private <C> C loadComponent(Class<?> componentClass, List<Class<?>> dependencyChain) {
        AppComponentInfo appComponentInfo = appComponentsByClass.get(componentClass);

        if (dependencyChain.contains(componentClass)) {
            throw new CircularDependencyException(appComponentInfo.name);
        } else {
            dependencyChain.add(componentClass);
        }

        Object[] methodParamObjects = prepareObjectsForMethodParams(appComponentInfo.creationMethod, dependencyChain);
        C appComponent = ReflectionHelper.callMethod(appComponentInfo.creationMethod, appComponentInfo.configClassObject, methodParamObjects);
        appComponentInfo.setAppComponent(appComponent);
        return appComponent;
    }

    private Object[] prepareObjectsForMethodParams(Method method, List<Class<?>> dependencies) {
        Class<?>[] methodParamClasses = method.getParameterTypes();
        Object[] methodParamObjects = new Object[methodParamClasses.length];

        for (int i = 0; i < methodParamClasses.length; i++) {
            Object methodParamObject = appComponentsByClass.get(methodParamClasses[i]).appComponent;
            if (methodParamObject == null) {
                methodParamObject = loadComponent(methodParamClasses[i], dependencies);
            }
            methodParamObjects[i] = methodParamObject;
        }
        return methodParamObjects;
    }

    private static class AppComponentInfo {
        private final Method creationMethod;
        private final Class<?> componentClass;
        private final Object configClassObject;
        private Object appComponent;
        private final String name;

        public void setAppComponent(Object appComponent) {
            this.appComponent = appComponent;
        }

        public <C> C getAppComponent() {
            return (C) this.appComponent;
        }

        AppComponentInfo(Method creationMethod, Class<?> componentClass, String name, Object configClassObject) {
            this.creationMethod = creationMethod;
            this.componentClass = componentClass;
            this.configClassObject = configClassObject;
            this.name = name;
            this.appComponent = null;
        }
    }
}

package com.example.appcontainer;

import com.example.appcontainer.api.AppComponent;
import com.example.appcontainer.api.AppComponentsContainer;
import com.example.appcontainer.api.AppComponentsContainerConfig;
import com.example.exception.*;
import com.example.helper.ReflectionHelper;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Method;
import java.util.*;

public class AppComponentsContainerImpl implements AppComponentsContainer {
    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?>...initialConfigClasses) {
        processConfig(initialConfigClasses);
    }

    public AppComponentsContainerImpl(String packageName) {
        Reflections reflections = new Reflections(packageName, Scanners.TypesAnnotated);
        Set<Class<?>> initialConfigClasses = reflections.getTypesAnnotatedWith(AppComponentsContainerConfig.class);
        processConfig(initialConfigClasses.toArray(new Class<?>[0]));
    }

    private void processConfig(Class<?>...configClasses) {
        Map<String, AppComponentDefinition> appComponentDefinitions = new HashMap<>();
        for (Class<?> configClass: configClasses) {
            checkConfigClass(configClass);
            Map<String, AppComponentDefinition> configClassComponentDefinitions = createComponentDefinitions(configClass);
            appComponentDefinitions.putAll(configClassComponentDefinitions);
        }
        createAppComponents(appComponentDefinitions);
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    private Map<String, AppComponentDefinition> createComponentDefinitions(Class<?> configClass) {
        Map<String, AppComponentDefinition> configClassComponentDefinitions = new HashMap<>();
        Object configClassObject = null;
        for (Method method: ReflectionHelper.getAnnotatedMethods(AppComponent.class, configClass)) {
            AppComponent appComponentAnnotation = method.getAnnotation(AppComponent.class);
            String componentName = appComponentAnnotation.name();

            if (configClassComponentDefinitions.containsKey(componentName)) {
                throw new ComponentAlreadyExistsException(componentName);
            }

            if (configClassObject == null) {
                configClassObject = ReflectionHelper.instantiate(configClass);
            }
            configClassComponentDefinitions.put(componentName, new AppComponentDefinition(method, componentName, configClassObject));
        }

        return configClassComponentDefinitions;
    }

    private void createAppComponents(Map<String, AppComponentDefinition> appComponentDefinitions) {
        boolean atLeastOneComponentCreated;
        do {
            atLeastOneComponentCreated = false;
            for (AppComponentDefinition appComponentDefinition : appComponentDefinitions.values()) {
                if (!appComponentsByName.containsKey(appComponentDefinition.name())) {
                    Object appComponent = createAppComponent(appComponentDefinition);
                    if (appComponent != null) {
                        appComponents.add(appComponent);
                        appComponentsByName.put(appComponentDefinition.name(), appComponent);
                        atLeastOneComponentCreated = true;
                    }
                }
            }
        } while (appComponentsByName.size() < appComponentDefinitions.size() && atLeastOneComponentCreated);

        if (appComponentDefinitions.size() > appComponentsByName.size()) {
            throw new AppComponentProcessException("Failed to find required objects to create components");
        }
    }

    private <C> C createAppComponent(AppComponentDefinition appComponentInfo) {
        Object[] methodParamObjects;
        try {
            methodParamObjects = prepareObjectsForMethodParams(appComponentInfo.creationMethod());
        } catch (NotFoundComponentForMethodParams e) {
            return null;
        }

        return ReflectionHelper.callMethod(
                appComponentInfo.creationMethod(),
                appComponentInfo.configClassObject(),
                methodParamObjects
        );
    }

    private Object[] prepareObjectsForMethodParams(Method method) {
        Class<?>[] methodParamClasses = method.getParameterTypes();
        Object[] methodParamObjects = new Object[methodParamClasses.length];

        for (int i = 0; i < methodParamClasses.length; i++) {
            Object appComponentForMethodParam = findAppComponentByComponentClass(methodParamClasses[i]);
            if (appComponentForMethodParam != null) {
                methodParamObjects[i] = appComponentForMethodParam;
            } else {
                throw new NotFoundComponentForMethodParams(methodParamClasses[i]);
            }
        }
        return methodParamObjects;
    }

    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        return Optional.ofNullable(findAppComponentByComponentClass(componentClass))
                .orElseThrow(() -> new NotFoundComponentException(componentClass));
    }

    @Override
    public <C> C getAppComponent(String componentName) {
        return Optional.ofNullable((C) appComponentsByName.get(componentName))
                .orElseThrow(() -> new NotFoundComponentException(componentName));
    }

    private <C> C findAppComponentByComponentClass(Class<C> componentClass) {
        List<Object> assignableComponents =
                appComponents.stream()
                        .filter(component -> componentClass.isAssignableFrom(component.getClass()))
                        .toList();

        if (assignableComponents.size() > 1) {
            throw new TooManyComponentException(componentClass);
        }

        return assignableComponents.isEmpty() ? null : (C) assignableComponents.get(0);
    }

    private record AppComponentDefinition(Method creationMethod, String name, Object configClassObject) { }
}

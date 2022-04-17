package com.example.core.repository.mapper;

import com.example.core.annotations.Id;
import com.example.core.repository.mapper.exception.NotFoundConstructorWithoutParamsException;
import com.example.core.repository.mapper.exception.NotFoundFieldIdException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {
    private final Class<T> clazz;
    private String className;
    private Constructor<T> classWithoutParamsConstructor;
    private Field idField;
    private List<Field> allClassFields;
    private List<Field> classFieldsWithoutIdField;

    public EntityClassMetaDataImpl(Class<T> entityClass) {
        this.clazz = entityClass;
    }

    @Override
    public String getName() {
        if (this.className == null) {
            this.className = this.clazz.getSimpleName();
        }
        return this.className;
    }

    @Override
    public Constructor<T> getConstructor() {
        if (this.classWithoutParamsConstructor == null) {
            try {
                this.classWithoutParamsConstructor = this.clazz.getConstructor();
            } catch (NoSuchMethodException e) {
                throw new NotFoundConstructorWithoutParamsException(this.clazz);
            }
        }
        return this.classWithoutParamsConstructor;
    }

    @Override
    public Field getFieldId() {
        if (this.idField == null) {
            this.idField =
                    Arrays.stream(clazz.getDeclaredFields())
                            .filter(item -> item.isAnnotationPresent(Id.class))
                            .findFirst()
                            .orElseThrow(() -> new NotFoundFieldIdException(clazz));
        }
        return this.idField;
    }

    @Override
    public List<Field> getAllFields() {
        if (this.allClassFields == null) {
            this.allClassFields =
                            Arrays.stream(this.clazz.getDeclaredFields())
                                    .filter(item -> ((item.getModifiers() & Modifier.STATIC) == 0)) // Не учитывать поля, которые IDEA вставляет в класс, когда проводит тестирование с анализом покрытия
                                    .toList();
        }
        return this.allClassFields;
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        if (this.classFieldsWithoutIdField == null) {
            this.classFieldsWithoutIdField =
                    Arrays.stream(this.clazz.getDeclaredFields())
                            .filter(item -> ((item.getModifiers() & Modifier.STATIC) == 0)) // Не учитывать поля, которые IDEA вставляет в класс, когда проводит тестирование с анализом покрытия
                            .filter(item -> !item.isAnnotationPresent(Id.class))
                            .toList();
        }
        return this.classFieldsWithoutIdField;
    }
}

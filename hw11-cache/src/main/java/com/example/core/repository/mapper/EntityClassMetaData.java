package com.example.core.repository.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;

public interface EntityClassMetaData<T> {
    String getName();

    Constructor<T> getConstructor();

    Field getFieldId();

    List<Field> getAllFields();

    List<Field> getFieldsWithoutId();
}

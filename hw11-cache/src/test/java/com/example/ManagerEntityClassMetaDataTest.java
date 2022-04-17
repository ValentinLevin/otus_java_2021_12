package com.example;

import com.example.core.repository.mapper.EntityClassMetaData;
import com.example.core.repository.mapper.EntityClassMetaDataImpl;
import com.example.crm.model.Manager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;

@DisplayName("Проверка EntityClassMetaData по работе с классом Manager, ")
class ManagerEntityClassMetaDataTest {
    private EntityClassMetaData<Manager> entityClassMetaData;

    @BeforeEach
    public void createEntityClassMetaData() {
        this.entityClassMetaData = new EntityClassMetaDataImpl<>(Manager.class);
    }

    @Test
    @DisplayName(" что класс вернет короткое имя класса, соответствующее имени таблицы")
    void classNameTest() {
        String expectedValue = "Manager";
        Assertions.assertThat(entityClassMetaData.getName()).isEqualTo(expectedValue);
    }

    @Test
    @DisplayName(" что класс вернет пустой конструктор класса Manager")
    void constructorTest() throws NoSuchMethodException {
        Constructor<Manager> expectedValue = Manager.class.getDeclaredConstructor();
        Assertions.assertThat(entityClassMetaData.getConstructor()).isEqualTo(expectedValue);
    }

    @Test
    @DisplayName(" что класс вернет все поля класса Manager")
    void allFieldsTest() throws NoSuchFieldException {
        Field noField = Manager.class.getDeclaredField("no");
        Field labelField = Manager.class.getDeclaredField("label");
        Field param1Field = Manager.class.getDeclaredField("param1");

        List<Field> actualFieldList = entityClassMetaData.getAllFields();

        Assertions.assertThat(actualFieldList)
                        .hasSize(3)
                        .contains(noField)
                        .contains(labelField)
                        .contains(param1Field);
    }

    @Test
    @DisplayName(" что будут возвращены поля, не являющиеся ключевыми")
    void fieldsWithoutIdFieldTest() throws NoSuchFieldException {
        Field labelField = Manager.class.getDeclaredField("label");
        Field param1Field = Manager.class.getDeclaredField("param1");

        List<Field> actualFieldList = entityClassMetaData.getFieldsWithoutId();

        Assertions.assertThat(actualFieldList)
                .hasSize(2)
                .contains(labelField)
                .contains(param1Field);
    }

    @Test
    @DisplayName(" что будет возвращено ключевое поле")
    void idFieldTest() throws NoSuchFieldException {
        Field expectedIdFieldValue = Manager.class.getDeclaredField("no");
        Field actualIdFieldValue = entityClassMetaData.getFieldId();

        Assertions.assertThat(actualIdFieldValue).isEqualTo(expectedIdFieldValue);
    }
}

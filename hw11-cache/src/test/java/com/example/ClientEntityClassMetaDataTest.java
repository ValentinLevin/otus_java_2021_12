package com.example;

import com.example.core.repository.mapper.EntityClassMetaData;
import com.example.core.repository.mapper.EntityClassMetaDataImpl;
import com.example.crm.model.Client;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;

@DisplayName("Проверка EntityClassMetaData по работе с классом Client, ")
class ClientEntityClassMetaDataTest {
    private EntityClassMetaData<Client> entityClassMetaData;

    @BeforeEach
    public void createEntityClassMetaData() {
        this.entityClassMetaData = new EntityClassMetaDataImpl<>(Client.class);
    }

    @Test
    @DisplayName(" что класс вернет короткое имя класса, соответствующее имени таблицы")
    void classNameTest() {
        String expectedValue = "Client";
        Assertions.assertThat(entityClassMetaData.getName()).isEqualTo(expectedValue);
    }

    @Test
    @DisplayName(" что класс вернет пустой конструктор класса Client")
    void constructorTest() throws NoSuchMethodException {
        Constructor<Client> expectedValue = Client.class.getDeclaredConstructor();
        Assertions.assertThat(entityClassMetaData.getConstructor()).isEqualTo(expectedValue);
    }

    @Test
    @DisplayName(" что класс вернет все поля класса Client")
    void allFieldsTest() throws NoSuchFieldException {
        Field idField = Client.class.getDeclaredField("id");
        Field nameField = Client.class.getDeclaredField("name");

        List<Field> actualFieldList = entityClassMetaData.getAllFields();

        Assertions.assertThat(actualFieldList)
                        .hasSize(2)
                        .contains(idField)
                        .contains(nameField);
    }

    @Test
    @DisplayName(" что будут возвращены поля, не являющиеся ключевыми")
    void fieldsWithoutIdFieldTest() throws NoSuchFieldException {
        Field nameField = Client.class.getDeclaredField("name");

        List<Field> actualFieldList = entityClassMetaData.getFieldsWithoutId();

        Assertions.assertThat(actualFieldList)
                .hasSize(1)
                .contains(nameField);
    }

    @Test
    @DisplayName(" что будет возвращено ключевое поле")
    void idFieldTest() throws NoSuchFieldException {
        Field expectedIdFieldValue = Client.class.getDeclaredField("id");
        Field actualIdFieldValue = entityClassMetaData.getFieldId();

        Assertions.assertThat(actualIdFieldValue).isEqualTo(expectedIdFieldValue);
    }
}

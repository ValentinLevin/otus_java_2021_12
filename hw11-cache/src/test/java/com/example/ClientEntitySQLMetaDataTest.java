package com.example;

import com.example.core.repository.mapper.EntityClassMetaData;
import com.example.core.repository.mapper.EntityClassMetaDataImpl;
import com.example.core.repository.mapper.EntitySQLMetaData;
import com.example.core.repository.mapper.EntitySQLMetaDataImpl;
import com.example.crm.model.Client;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Проверка класса EntitySQLMetaData по работе с классом Client")
class ClientEntitySQLMetaDataTest {
    private EntitySQLMetaData<Client> entitySQLMetaData;

    @BeforeEach
    void setUp() {
        EntityClassMetaData<Client> entityClassMetaData = new EntityClassMetaDataImpl<>(Client.class);
        this.entitySQLMetaData = new EntitySQLMetaDataImpl<>(entityClassMetaData);
    }

    @Test
    @DisplayName(" что генерируется корректный select запрос всех полей")
    void selectAllSqlTest() {
        String expectedValue = "select id, name from client";
        String actualValue = this.entitySQLMetaData.getSelectAllSql();
        Assertions.assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    @DisplayName(" что генерируется корректный select запрос всех полей записи с конкретным id")
    void selectByIdSqlTest() {
        String expectedValue = "select id, name from client where id = ?";
        String actualValue = this.entitySQLMetaData.getSelectByIdSql();
        Assertions.assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    @DisplayName(" что генерируется корректный insert запрос")
    void insertSqlTest() {
        String expectedValue = "insert into client(name) values(?)";
        String actualValue = this.entitySQLMetaData.getInsertSql();
        Assertions.assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    @DisplayName(" что генерируется корректный update запрос")
    void updateSqlTest() {
        String expectedValue = "update client set name = ? where id = ?";
        String actualValue = this.entitySQLMetaData.getUpdateSql();
        Assertions.assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    @DisplayName(" что генерируется корректный delete запрос")
    void deleteSqlTest() {
        String expectedValue = "delete from client where id = ?";
        String actualValue = this.entitySQLMetaData.getDeleteSql();
        Assertions.assertThat(actualValue).isEqualTo(expectedValue);
    }
}

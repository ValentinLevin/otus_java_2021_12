package com.example;

import com.example.core.repository.mapper.EntityClassMetaData;
import com.example.core.repository.mapper.EntityClassMetaDataImpl;
import com.example.core.repository.mapper.EntitySQLMetaData;
import com.example.core.repository.mapper.EntitySQLMetaDataImpl;
import com.example.crm.model.Manager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Проверка класса EntitySQLMetaData по работе с классом Manager")
class ManagerEntitySQLMetaDataTest {
    private EntitySQLMetaData<Manager> entitySQLMetaData;

    @BeforeEach
    void setUp() {
        EntityClassMetaData<Manager> entityClassMetaData = new EntityClassMetaDataImpl<>(Manager.class);
        this.entitySQLMetaData = new EntitySQLMetaDataImpl<>(entityClassMetaData);
    }

    @Test
    @DisplayName(" что генерируется корректный select запрос всех полей")
    void selectAllSqlTest() {
        String expectedValue = "select no, label, param1 from manager";
        String actualValue = this.entitySQLMetaData.getSelectAllSql();
        Assertions.assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    @DisplayName(" что генерируется корректный select запрос всех полей записи с конкретным id")
    void selectByIdSqlTest() {
        String expectedValue = "select no, label, param1 from manager where no = ?";
        String actualValue = this.entitySQLMetaData.getSelectByIdSql();
        Assertions.assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    @DisplayName(" что генерируется корректный insert запрос")
    void insertSqlTest() {
        String expectedValue = "insert into manager(label, param1) values(?, ?)";
        String actualValue = this.entitySQLMetaData.getInsertSql();
        Assertions.assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    @DisplayName(" что генерируется корректный update запрос")
    void updateSqlTest() {
        String expectedValue = "update manager set label = ?, param1 = ? where no = ?";
        String actualValue = this.entitySQLMetaData.getUpdateSql();
        Assertions.assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    @DisplayName(" что генерируется корректный delete запрос")
    void deleteSqlTest() {
        String expectedValue = "delete from manager where no = ?";
        String actualValue = this.entitySQLMetaData.getDeleteSql();
        Assertions.assertThat(actualValue).isEqualTo(expectedValue);
    }
}

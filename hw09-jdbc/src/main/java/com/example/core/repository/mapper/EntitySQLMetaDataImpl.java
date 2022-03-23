package com.example.core.repository.mapper;

import java.lang.reflect.Field;
import java.util.stream.Collectors;

public class EntitySQLMetaDataImpl<T> implements EntitySQLMetaData<T> {
    private final EntityClassMetaData<T> entityClassMetaData;

    private String selectAllSql;
    private String selectByIdSql;
    private String insertSql;
    private String updateSql;
    private String deleteSql;

    public EntitySQLMetaDataImpl(EntityClassMetaData<T> entityClassMetaData) {
        this.entityClassMetaData = entityClassMetaData;
    }

    private String generateSelectAllSql() {
        String sql =
                "select " +
                this.entityClassMetaData.getAllFields()
                        .stream()
                        .map(Field::getName)
                        .collect(Collectors.joining(", ")) +
                " from " + this.entityClassMetaData.getName();
        return sql.toLowerCase();
    }

    private String generateSelectByIdSql() {
        String sql =
                "select " +
                this.entityClassMetaData.getAllFields()
                        .stream()
                        .map(Field::getName)
                        .collect(Collectors.joining(", ")) +
                " from " + this.entityClassMetaData.getName() +
                " where " + this.entityClassMetaData.getFieldId().getName() + " = ?";
        return sql.toLowerCase();
    }

    private String generateInsertSql() {
        String sql =
                "insert into " + this.entityClassMetaData.getName() + "(" +
                        this.entityClassMetaData.getFieldsWithoutId()
                                .stream()
                                .map(Field::getName)
                                .collect(Collectors.joining(", "))
                + ") "
                + "values(" +
                        this.entityClassMetaData.getFieldsWithoutId()
                                .stream()
                                .map(item -> "?")
                                .collect(Collectors.joining(", "))
                + ")";
        return sql.toLowerCase();
    }

    private String generateUpdateSql() {
        String sql =
                "update " + this.entityClassMetaData.getName() + " " +
                        "set " +
                        this.entityClassMetaData.getFieldsWithoutId()
                                .stream()
                                .map(item -> item.getName() + " = ?")
                                .collect(Collectors.joining(", ")) + " " +
                "where " + this.entityClassMetaData.getFieldId().getName() + " = ?";
        return sql.toLowerCase();
    }

    private String generateDeleteSql() {
        String sql =
                "delete from " + this.entityClassMetaData.getName() + " " +
                "where " + this.entityClassMetaData.getFieldId().getName() + " = ?";
        return sql.toLowerCase();
    }

    @Override
    public String getSelectAllSql() {
        if (selectAllSql == null) {
            this.selectAllSql = generateSelectAllSql();
        }
        return selectAllSql;
    }

    @Override
    public String getSelectByIdSql() {
        if (selectByIdSql == null) {
            this.selectByIdSql = generateSelectByIdSql();
        }
        return this.selectByIdSql;
    }

    @Override
    public String getInsertSql() {
        if (insertSql == null) {
            insertSql = generateInsertSql();
        }
        return insertSql;
    }

    @Override
    public String getUpdateSql() {
        if (updateSql == null) {
            updateSql = generateUpdateSql();
        }
        return updateSql;
    }

    @Override
    public String getDeleteSql() {
        if (deleteSql == null) {
            deleteSql = generateDeleteSql();
        }
        return deleteSql;
    }
}

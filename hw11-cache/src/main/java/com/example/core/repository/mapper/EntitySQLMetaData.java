package com.example.core.repository.mapper;

public interface EntitySQLMetaData<T> {
    String getSelectAllSql();

    String getSelectByIdSql();

    String getInsertSql();

    String getUpdateSql();

    String getDeleteSql();
}

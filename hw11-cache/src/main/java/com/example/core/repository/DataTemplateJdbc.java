package com.example.core.repository;

import com.example.core.repository.executor.DbExecutor;
import com.example.core.repository.mapper.EntityClassMetaData;
import com.example.core.repository.mapper.EntitySQLMetaData;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Сохраняет объект в базу, читает объект из базы
 */
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntityClassMetaData<T> entityClassMetaData;
    private final EntitySQLMetaData<T> entitySQLMetaData;

    public DataTemplateJdbc(
            DbExecutor dbExecutor,
            EntityClassMetaData<T> entityClassMetaData,
            EntitySQLMetaData<T> entitySQLMetaData
    ) {
        this.dbExecutor = dbExecutor;
        this.entityClassMetaData = entityClassMetaData;
        this.entitySQLMetaData = entitySQLMetaData;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        String sql = this.entitySQLMetaData.getSelectByIdSql();
        return dbExecutor.executeSelect(connection, sql, Collections.singletonList(id), (ResultSet rs) -> {
            try {
                if (rs.next()) {
                    Constructor<T> entityConstructor = entityClassMetaData.getConstructor();
                    List<Field> fieldsToSet = entityClassMetaData.getAllFields();
                    return buildEntity(rs, entityConstructor, fieldsToSet);
                } else {
                    return null;
                }
            } catch (SQLException e) {
                throw new DataTemplateException(e);
            }
        });
    }

    @Override
    public List<T> findAll(Connection connection) {
        String sql = this.entitySQLMetaData.getSelectAllSql();
        return dbExecutor.executeSelect(connection, sql, Collections.emptyList(), (ResultSet rs) -> {
            List<T> entityList = new ArrayList<>();
            Constructor<T> entityConstructor = entityClassMetaData.getConstructor();
            List<Field> fieldsToSet = entityClassMetaData.getAllFields();

            try {
                while (rs.next()) {
                    entityList.add(buildEntity(rs, entityConstructor, fieldsToSet));
                }
            } catch (SQLException e) {
                throw new DataTemplateException(e);
            }

            return entityList;
        }).orElse(Collections.emptyList());
    }

    @Override
    public long insert(Connection connection, T entity) {
        String sql = this.entitySQLMetaData.getInsertSql();
        List<Field> fieldsToInsert = this.entityClassMetaData.getFieldsWithoutId();
        List<Object> paramValues = getParamsFromEntityByFields(entity, fieldsToInsert);
        return this.dbExecutor.executeStatement(connection, sql, paramValues);
    }

    @Override
    public void update(Connection connection, T entity) {
        String sql = this.entitySQLMetaData.getUpdateSql();
        List<Field> fieldsToUpdate = this.entityClassMetaData.getFieldsWithoutId();
        List<Object> paramValues = getParamsFromEntityByFields(entity, fieldsToUpdate);
        paramValues.add(getParamValue(entity, this.entityClassMetaData.getFieldId()));
        this.dbExecutor.executeStatement(connection, sql, paramValues);
    }

    @Override
    public void delete(Connection connection, long id) {
        String sql = this.entitySQLMetaData.getDeleteSql();
        this.dbExecutor.executeStatement(connection, sql, Collections.singletonList(id));
    }

    private Object getParamValue(T entity, Field field) {
        try {
            field.setAccessible(true);
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new DataTemplateException(e);
        }
    }

    private List<Object> getParamsFromEntityByFields(T entity, List<Field> fields) {
        List<Object> params = new ArrayList<>();
        fields.forEach(item -> params.add(getParamValue(entity, item)));
        return params;
    }

    private T buildEntity(ResultSet rs, Constructor<T> entityConstructor, List<Field> fieldsToSet) {
        T entity;
        try {
            entity = entityConstructor.newInstance();
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }

        for (Field field : fieldsToSet) {
            String fieldName = field.getName();
            try {
                field.setAccessible(true);
                field.set(entity, rs.getObject(fieldName));
            } catch (Exception e) {
                throw new DataTemplateException(e);
            }
        }

        return entity;
    }
}

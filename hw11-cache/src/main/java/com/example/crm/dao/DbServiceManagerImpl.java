package com.example.crm.dao;

import com.example.cache.HwCache;
import com.example.core.repository.DataTemplateJdbc;
import com.example.core.sessionmanager.TransactionRunner;
import com.example.crm.model.Manager;

import java.util.List;
import java.util.Optional;

public class DbServiceManagerImpl implements DbServiceManager {
    private final HwCache<String, Manager> cache;
    private final TransactionRunner transactionRunner;
    private final DataTemplateJdbc<Manager> dataTemplate;

    public DbServiceManagerImpl(
            TransactionRunner transactionRunner,
            DataTemplateJdbc<Manager> dataTemplate,
            HwCache<String, Manager> cache
    ) {
        this.transactionRunner = transactionRunner;
        this.dataTemplate = dataTemplate;
        this.cache = cache;
    }

    public DbServiceManagerImpl(
            TransactionRunner transactionRunner,
            DataTemplateJdbc<Manager> dataTemplate
    ) {
        this(transactionRunner, dataTemplate, null);
    }

    @Override
    public Manager saveManager(Manager manager) {
        long managerNo = transactionRunner.doInTransaction(connection -> {
            if (manager.getNo() == null || manager.getNo() == 0) {
                return dataTemplate.insert(connection, manager);
            } else {
                dataTemplate.update(connection, manager);
                return manager.getNo();
            }
        });

        Manager managerAfterSave = this.getManager(managerNo).orElse(null);
        saveIntoCache(managerAfterSave);
        return managerAfterSave;
    }

    @Override
    public Optional<Manager> getManager(long id) {
        Manager manager = getFromCache(id);
        if (manager == null) {
            Optional<Manager> managerFromDB =
                    transactionRunner.doInTransaction(connection -> dataTemplate.findById(connection, id));
            managerFromDB.ifPresent(this::saveIntoCache);
            return managerFromDB;
        } else {
            return Optional.of(manager);
        }
    }

    @Override
    public List<Manager> findAll() {
        List<Manager> managers = transactionRunner.doInTransaction(dataTemplate::findAll);
        managers.forEach(this::saveIntoCache);
        return managers;
    }

    @Override
    public void deleteManager(long id) {
        transactionRunner.doInTransaction(connection -> {
            dataTemplate.delete(connection, id);
            removeFromCache(id);
            return null;
        });
    }

    private void saveIntoCache(Manager manager) {
        if (cache != null && manager != null) {
            this.cache.put(String.valueOf(manager.getNo()), manager);
        }
    }

    private void removeFromCache(Long id) {
        if (this.cache != null) {
            this.cache.remove(String.valueOf(id));
        }
    }

    private Manager getFromCache(Long id) {
        return this.cache == null ? null : this.cache.get(String.valueOf(id));
    }
}
package com.example.crm.service;

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
            if (Optional.ofNullable(manager.getNo()).orElse(0L) == 0) {
                return dataTemplate.insert(connection, manager);
            } else {
                dataTemplate.update(connection, manager);
                return manager.getNo();
            }
        });

        Manager managerAfterSave = this.getManager(managerNo).orElse(null);
        if (managerAfterSave != null && this.cache != null) {
            saveIntoCache(managerAfterSave.getNo(), managerAfterSave);
        }
        return managerAfterSave;
    }

    @Override
    public Optional<Manager> getManager(long id) {
        Manager manager = this.cache == null ? null : this.cache.get(String.valueOf(id));
        if (manager == null) {
            return transactionRunner.doInTransaction(connection -> dataTemplate.findById(connection, id));
        } else {
            return Optional.of(manager);
        }
    }

    @Override
    public List<Manager> findAll() {
        List<Manager> managers = transactionRunner.doInTransaction(dataTemplate::findAll);
        if (this.cache != null) {
            managers.forEach(manager -> saveIntoCache(manager.getNo(), manager));
        }
        return managers;
    }

    @Override
    public void deleteManager(long id) {
        transactionRunner.doInTransaction(connection -> {
            dataTemplate.delete(connection, id);
            if (this.cache != null) {
                this.cache.remove(String.valueOf(id));
            }
            return null;
        });
    }

    private void saveIntoCache(Long id, Manager manager) {
        this.cache.put(String.valueOf(id), manager);
    }
}

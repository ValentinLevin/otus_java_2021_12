package com.example.crm.service;

import com.example.core.repository.DataTemplateJdbc;
import com.example.core.sessionmanager.TransactionRunner;
import com.example.crm.model.Manager;

import java.util.List;
import java.util.Optional;

public class DbServiceManagerImpl implements DbServiceManager {
    private final TransactionRunner transactionRunner;
    private final DataTemplateJdbc<Manager> dataTemplate;

    public DbServiceManagerImpl(TransactionRunner transactionRunner, DataTemplateJdbc<Manager> dataTemplate) {
        this.transactionRunner = transactionRunner;
        this.dataTemplate = dataTemplate;
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
        return this.getManager(managerNo).orElse(null);
    }

    @Override
    public Optional<Manager> getManager(long id) {
        return transactionRunner.doInTransaction(connection -> dataTemplate.findById(connection, id));
    }

    @Override
    public List<Manager> findAll() {
        return transactionRunner.doInTransaction(dataTemplate::findAll);
    }

    @Override
    public void deleteManager(long id) {
        transactionRunner.doInTransaction(connection -> {
            dataTemplate.delete(connection, id);
            return null;
        });
    }
}

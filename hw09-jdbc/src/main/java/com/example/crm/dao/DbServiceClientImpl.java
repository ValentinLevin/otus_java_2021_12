package com.example.crm.dao;

import com.example.core.repository.DataTemplateJdbc;
import com.example.core.sessionmanager.TransactionRunner;
import com.example.crm.model.Client;

import java.util.List;
import java.util.Optional;

public class DbServiceClientImpl implements DbServiceClient {
    private final TransactionRunner transactionRunner;
    private final DataTemplateJdbc<Client> dataTemplate;

    public DbServiceClientImpl(TransactionRunner transactionRunner, DataTemplateJdbc<Client> dataTemplate) {
        this.transactionRunner = transactionRunner;
        this.dataTemplate = dataTemplate;
    }

    @Override
    public Client saveClient(Client client) {
        long clientId = transactionRunner.doInTransaction(connection -> {
            if (Optional.ofNullable(client.getId()).orElse(0L) == 0) {
                return dataTemplate.insert(connection, client);
            } else {
                dataTemplate.update(connection, client);
                return client.getId();
            }

        });
        return this.getClient(clientId).orElse(null);
    }

    @Override
    public Optional<Client> getClient(long id) {
        return transactionRunner.doInTransaction(connection -> dataTemplate.findById(connection, id));
    }

    @Override
    public List<Client> findAll() {
        return transactionRunner.doInTransaction(dataTemplate::findAll);
    }

    @Override
    public void deleteClient(long id) {
        transactionRunner.doInTransaction(connection -> {
            dataTemplate.delete(connection, id);
            return null;
        });
    }
}

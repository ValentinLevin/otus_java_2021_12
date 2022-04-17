package com.example.crm.service;

import com.example.cache.HwCache;
import com.example.core.repository.DataTemplateJdbc;
import com.example.core.sessionmanager.TransactionRunner;
import com.example.crm.model.Client;

import java.util.List;
import java.util.Optional;

public class DbServiceClientImpl implements DbServiceClient {
    private final HwCache<String, Client> cache;
    private final TransactionRunner transactionRunner;
    private final DataTemplateJdbc<Client> dataTemplate;

    public DbServiceClientImpl(
            TransactionRunner transactionRunner,
            DataTemplateJdbc<Client> dataTemplate,
            HwCache<String, Client> cache
    ) {
        this.transactionRunner = transactionRunner;
        this.dataTemplate = dataTemplate;
        this.cache = cache;
    }

    public DbServiceClientImpl(
            TransactionRunner transactionRunner,
            DataTemplateJdbc<Client> dataTemplate
    ) {
        this(transactionRunner, dataTemplate, null);
    }

    @Override
    public Client saveClient(Client client) {
        long clientId = transactionRunner.doInTransaction(connection -> {
            if (client.getId() == null || client.getId() == 0) {
                return dataTemplate.insert(connection, client);
            } else {
                dataTemplate.update(connection, client);
                return client.getId();
            }
        });

        Client clientAfterSave = this.getClient(clientId).orElse(null);
        saveIntoCache(clientAfterSave);
        return clientAfterSave;
    }

    @Override
    public Optional<Client> getClient(long id) {
        Client client = getFromCache(id);
        if (client == null) {
            Optional<Client> clientFromDB =
                    transactionRunner.doInTransaction(connection -> dataTemplate.findById(connection, id));
            clientFromDB.ifPresent(this::saveIntoCache);
            return clientFromDB;
        } else {
            return Optional.of(client);
        }
    }

    @Override
    public List<Client> findAll() {
        List<Client> clients = transactionRunner.doInTransaction(dataTemplate::findAll);
        clients.forEach(this::saveIntoCache);
        return clients;
    }

    @Override
    public void deleteClient(long id) {
        transactionRunner.doInTransaction(connection -> {
            dataTemplate.delete(connection, id);
            removeFromCache(id);
            return null;
        });
    }

    private void saveIntoCache(Client client) {
        if (cache != null && client != null) {
            this.cache.put(String.valueOf(client.getId()), client);
        }
    }

    private void removeFromCache(Long id) {
        if (this.cache != null) {
            this.cache.remove(String.valueOf(id));
        }
    }

    private Client getFromCache(Long id) {
        return this.cache == null ? null : this.cache.get(String.valueOf(id));
    }
}

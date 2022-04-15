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
            if (Optional.ofNullable(client.getId()).orElse(0L) == 0) {
                return dataTemplate.insert(connection, client);
            } else {
                dataTemplate.update(connection, client);
                return client.getId();
            }
        });

        Client clientAfterSave = this.getClient(clientId).orElse(null);
        if (clientAfterSave != null && cache != null) {
            saveIntoCache(clientAfterSave.getId(), clientAfterSave);
        }
        return clientAfterSave;
    }

    @Override
    public Optional<Client> getClient(long id) {
        Client client = this.cache == null ? null : this.cache.get(String.valueOf(id));
        if (client == null) {
            return transactionRunner.doInTransaction(connection -> dataTemplate.findById(connection, id));
        } else {
            return Optional.of(client);
        }
    }

    @Override
    public List<Client> findAll() {
        List<Client> clients = transactionRunner.doInTransaction(dataTemplate::findAll);
        if (this.cache != null) {
            clients.forEach(client -> saveIntoCache(client.getId(), client));
        }
        return clients;
    }

    @Override
    public void deleteClient(long id) {
        transactionRunner.doInTransaction(connection -> {
            dataTemplate.delete(connection, id);
            if (this.cache != null) {
                this.cache.remove(String.valueOf(id));
            }
            return null;
        });
    }

    private void saveIntoCache(Long id, Client client) {
        this.cache.put(String.valueOf(id), client);
    }
}

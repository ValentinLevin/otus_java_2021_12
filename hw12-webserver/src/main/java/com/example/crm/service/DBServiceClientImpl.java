package com.example.crm.service;

import com.example.core.repository.DataTemplate;
import com.example.core.sessionmanager.TransactionManager;
import com.example.crm.model.Client;

import java.util.List;
import java.util.Optional;

public class DBServiceClientImpl implements DBServiceClient {
    private final DataTemplate<Client> clientDataTemplate;
    private final TransactionManager transactionManager;

    public DBServiceClientImpl(TransactionManager transactionManager, DataTemplate<Client> clientDataTemplate) {
        this.clientDataTemplate = clientDataTemplate;
        this.transactionManager = transactionManager;
    }

    @Override
    public Client saveClient(Client client) {
        return transactionManager.doInTransaction(session -> {
            var clientCloned = client.clone();
            if (client.getId() == null || client.getId() == 0) {
                clientDataTemplate.insert(session, clientCloned);
            } else {
                clientDataTemplate.update(session, clientCloned);
            }
            return clientCloned;
        });
    }

    @Override
    public Optional<Client> getClient(long id) {
        return this.transactionManager.doInReadOnlyTransaction(session ->
            clientDataTemplate.findById(session, id)
        );
    }

    @Override
    public List<Client> findAll() {
        return this.transactionManager.doInReadOnlyTransaction(clientDataTemplate::findAll);
    }
}

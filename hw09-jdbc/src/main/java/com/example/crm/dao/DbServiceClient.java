package com.example.crm.dao;

import com.example.crm.model.Client;

import java.util.List;
import java.util.Optional;

public interface DbServiceClient {
    Client saveClient(Client client);

    Optional<Client> getClient(long id);

    List<Client> findAll();

    void deleteClient(long id);
}

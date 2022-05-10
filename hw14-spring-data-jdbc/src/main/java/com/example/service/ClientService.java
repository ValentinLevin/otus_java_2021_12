package com.example.service;

import com.example.dto.ClientDTO;
import com.example.model.Client;

import java.util.List;
import java.util.Optional;

public interface ClientService {
    ClientDTO saveClient(ClientDTO clientDTO);
    Optional<ClientDTO> getClient(long id);
    List<ClientDTO> findAll();
}

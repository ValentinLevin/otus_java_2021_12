package com.example.service;

import com.example.dto.ClientDTO;
import com.example.mapper.ClientMapper;
import com.example.model.Client;
import com.example.repository.ClientRepository;
import com.example.sessionmanager.TransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final TransactionManager transactionManager;

    public ClientServiceImpl(ClientRepository clientRepository, TransactionManager transactionManager) {
        this.clientRepository = clientRepository;
        this.transactionManager = transactionManager;
    }

    @Override
    @Transactional
    public ClientDTO saveClient(ClientDTO clientDTO) {
        Client client = ClientMapper.fromClientDTO(clientDTO);
        Client savedClient = transactionManager.doInTransaction(() -> this.clientRepository.save(client));
        return ClientMapper.toClientDTO(savedClient);
    }

    @Override
    public Optional<ClientDTO> getClient(long id) {
        Client client = this.clientRepository.findById(id).orElse(null);
        return Optional.ofNullable(ClientMapper.toClientDTO(client));
    }

    @Override
    public List<ClientDTO> findAll() {
        List<ClientDTO> clients = new ArrayList<>();
        this.clientRepository.findAll().forEach(client -> clients.add(ClientMapper.toClientDTO(client)));
        return clients;
    }
}

package com.example.services;

import com.example.dto.ClientDTO;

import java.util.List;

public interface ClientService {
    List<ClientDTO> findAll();
    ClientDTO findById(Long id);
    void addClient(String name, String address, String[] phones);
}

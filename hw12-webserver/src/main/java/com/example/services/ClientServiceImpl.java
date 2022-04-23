package com.example.services;

import com.example.crm.service.DBServiceClient;
import com.example.crm.model.Address;
import com.example.crm.model.Client;
import com.example.crm.model.Phone;
import com.example.dto.ClientDTO;
import com.example.mapper.ClientMapper;

import java.util.*;

public class ClientServiceImpl implements ClientService {
    private final DBServiceClient dbServiceClient;

    public ClientServiceImpl(DBServiceClient dbServiceClient) {
        this.dbServiceClient = dbServiceClient;
    }

    @Override
    public List<ClientDTO> findAll() {
        List<Client> clients = Optional.ofNullable(this.dbServiceClient.findAll()).orElse(Collections.emptyList());
        return clients.stream().map(ClientMapper::toClientDTO).toList();
    }

    @Override
    public ClientDTO findById(Long id) {
        Client client = this.dbServiceClient.getClient(id).orElse(null);
        return ClientMapper.toClientDTO(client);
    }

    @Override
    public void addClient(String clientName, String street, String[] phoneNumbers) {
        if (
                clientName != null && !clientName.isEmpty()
                && street != null && !street.isEmpty()
        ) {
            Client client = new Client(clientName);

            Address address = new Address(street);
            address.setClient(client);
            client.setAddress(address);

            List<Phone> phones = Arrays.stream(Optional.ofNullable(phoneNumbers).orElse(new String[]{}))
                    .map(phoneNumber -> {
                            Phone phone = new Phone(phoneNumber);
                            phone.setClient(client);
                            return phone;

                    }).toList();
            client.setPhones(phones);

            dbServiceClient.saveClient(client);
        }
    }
}

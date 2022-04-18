package com.example.mapper;

import com.example.crm.model.Address;
import com.example.crm.model.Client;
import com.example.crm.model.Phone;
import com.example.dto.AddressDTO;
import com.example.dto.ClientDTO;
import com.example.dto.PhoneDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientMapper {
    private ClientMapper(){}

    public static Client fromClientDTO(ClientDTO clientDTO) {
        if (clientDTO != null) {
            Client client = new Client(clientDTO.getId(), clientDTO.getName());

            Address address = AddressMapper.fromAddressDTO(clientDTO.getAddress());
            address.setClient(client);
            client.setAddress(address);

            List<Phone> phones = Optional.ofNullable(clientDTO.getPhones()).orElse(new ArrayList<>())
                    .stream()
                    .map(phoneDTO -> {
                        Phone phone = PhoneMapper.fromPhoneDTO(phoneDTO);
                        phone.setClient(client);
                        return phone;
                    })
                    .toList();
            client.setPhones(phones);

            return client;
        }
        return null;
    }

    public static ClientDTO toClientDTO(Client client) {
        if (client != null) {
            AddressDTO addressDTO = AddressMapper.toAddressDTO(client.getAddress());
            List<PhoneDTO> phoneDTOs =
                    Optional.ofNullable(client.getPhones()).orElse(new ArrayList<>())
                            .stream()
                            .map(PhoneMapper::toPhoneDTO)
                            .toList();
            return new ClientDTO(client.getId(), client.getName(), addressDTO, phoneDTOs);
        }
        return null;
    }
}

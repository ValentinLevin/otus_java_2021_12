package com.example.mapper;

import com.example.model.Address;
import com.example.model.Client;
import com.example.model.Phone;
import com.example.dto.AddressDTO;
import com.example.dto.ClientDTO;
import com.example.dto.PhoneDTO;

import java.util.*;
import java.util.stream.Collectors;

public class ClientMapper {
    private ClientMapper(){}

    public static Client fromClientDTO(ClientDTO clientDTO) {
        if (clientDTO != null) {
            Address address = AddressMapper.fromAddressDTO(clientDTO.getAddress());

            Set<Phone> phones = Optional.ofNullable(clientDTO.getPhones()).orElse(new ArrayList<>())
                    .stream()
                    .map(phoneDTO -> PhoneMapper.fromPhoneDTO(phoneDTO))
                    .collect(Collectors.toSet());

            return new Client(clientDTO.getId(), clientDTO.getName(), address, phones);
        }
        return null;
    }

    public static ClientDTO toClientDTO(Client client) {
        if (client != null) {
            AddressDTO addressDTO = AddressMapper.toAddressDTO(client.getAddress());
            List<PhoneDTO> phoneDTOs =
                    Optional.ofNullable(client.getPhones()).orElse(Collections.emptySet())
                            .stream()
                            .map(PhoneMapper::toPhoneDTO)
                            .toList();
            return new ClientDTO(client.getId(), client.getName(), addressDTO, phoneDTOs);
        }
        return null;
    }
}

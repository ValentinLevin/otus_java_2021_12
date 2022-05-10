package com.example.mapper;

import com.example.model.Address;
import com.example.dto.AddressDTO;
import com.example.model.Client;

import java.util.HashSet;

public class AddressMapper {
    private AddressMapper(){}

    public static AddressDTO toAddressDTO(Address address) {
        return address != null ? new AddressDTO(address.getId(), address.getStreet()) : null;
    }

    public static Address fromAddressDTO(AddressDTO addressDTO) {
        return addressDTO != null ?  new Address(addressDTO.getId(), addressDTO.getStreet()) : null;
    }
}

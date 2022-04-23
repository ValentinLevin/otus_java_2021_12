package com.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ClientDTO {
    @JsonProperty("id")
    private long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("address")
    private AddressDTO address;

    @JsonProperty("phones")
    private List<PhoneDTO> phones;

    public ClientDTO(
            long id,
            String name,
            AddressDTO address,
            List<PhoneDTO> phones
    ) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phones = phones;
    }
}

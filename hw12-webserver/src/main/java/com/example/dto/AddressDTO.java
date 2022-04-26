package com.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AddressDTO {
    @JsonProperty("id")
    private long id;

    @JsonProperty("street")
    private String street;

    public AddressDTO(
            long id,
            String street
    ) {
        this.id = id;
        this.street = street;
    }
}

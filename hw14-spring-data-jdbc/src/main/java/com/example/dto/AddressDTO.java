package com.example.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
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

    @JsonCreator()
    public AddressDTO(
            @JsonProperty(value = "id", defaultValue = "0") long id,
            @JsonProperty("street") String street
    ) {
        this.id = id;
        this.street = street;
    }
}

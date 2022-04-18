package com.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PhoneDTO {
    @JsonProperty("id")
    private long id;

    @JsonProperty("number")
    private String number;

    public PhoneDTO(
            long id,
            String number
    ) {
        this.id = id;
        this.number = number;
    }
}

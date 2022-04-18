package com.example.mapper;

import com.example.crm.model.Phone;
import com.example.dto.PhoneDTO;

public class PhoneMapper {
    private PhoneMapper() {}

    public static PhoneDTO toPhoneDTO(Phone phone) {
        return phone != null ? new PhoneDTO(phone.getId(), phone.getNumber()) : null;
    }

    public static Phone fromPhoneDTO(PhoneDTO phoneDTO) {
        return phoneDTO != null ? new Phone(phoneDTO.getId(), phoneDTO.getNumber()) : null;
    }
}

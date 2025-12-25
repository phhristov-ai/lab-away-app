package com.labaway.backend.transformer.order;

import com.labaway.backend.dto.order.AddressDto;
import com.labaway.backend.dto.order.AddressEmailDto;
import com.labaway.backend.entity.order.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressTransformer {

    public AddressDto toDto(Address address) {
        if (address == null) {
            return null;
        }

        return AddressDto.builder()
                .firstName(address.getFirstName())
                .lastName(address.getLastName())
                .country(address.getCountry())
                .address(address.getStreetAddress())
                .city(address.getCity())
                .postCode(address.getPostCode())
                .phone(address.getPhone())
                .build();
    }

    public Address toEntity(AddressDto dto) {
        if (dto == null) {
            return null;
        }

        return Address.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .country(dto.getCountry())
                .streetAddress(dto.getAddress())
                .city(dto.getCity())
                .postCode(dto.getPostCode())
                .phone(dto.getPhone())
                .build();
    }
}

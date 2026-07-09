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

        return new AddressDto(
                address.getFirstName(),
                address.getLastName(),
                address.getCountry(),
                address.getStreetAddress(),
                address.getCity(),
                address.getPostCode(),
                address.getPhone()
        );
    }

    public Address toEntity(AddressDto dto) {
        if (dto == null) {
            return null;
        }

        return Address.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .country(dto.country())
                .streetAddress(dto.address())
                .city(dto.city())
                .postCode(dto.postCode())
                .phone(dto.phone())
                .build();
    }
}

package com.labaway.backend.dto.order;

public record AddressEmailDto(
        String firstName,
        String lastName,
        String streetAddress,
        String city,
        String postCode,
        String country,
        String phone
) {}

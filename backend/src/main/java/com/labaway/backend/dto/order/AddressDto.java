package com.labaway.backend.dto.order;

import jakarta.validation.constraints.NotBlank;

public record AddressDto(
        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "Country is required")
        String country,

        @NotBlank(message = "Address is required")
        String address,

        @NotBlank(message = "City is required")
        String city,

        @NotBlank(message = "Post code is required")
        String postCode,

        @NotBlank(message = "Phone number is required")
        String phone
) {}
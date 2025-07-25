package com.labaway.backend.transformer;
import com.labaway.backend.dto.order.AddressDto;
import com.labaway.backend.entity.order.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AddressTransformerTest {

    private AddressTransformer transformer;

    @BeforeEach
    void setUp() {
        transformer = new AddressTransformer();
    }

    @Test
    void toDto_shouldReturnDtoWithSameValues() {
        Address address = createSampleAddress("John", "Doe", "USA", "123 Main St", "New York", "10001");

        AddressDto dto = transformer.toDto(address);

        assertAddressDto(dto, "John", "Doe", "USA", "123 Main St", "New York", "10001");
    }

    @Test
    void toEntity_shouldReturnEntityWithSameValues() {
        AddressDto dto = createSampleAddressDto("Jane", "Smith", "UK", "456 High St", "London", "EC1A 1BB");

        Address entity = transformer.toEntity(dto);

        assertAddressEntity(entity, "Jane", "Smith", "UK", "456 High St", "London", "EC1A 1BB");
    }

    @Test
    void toDto_shouldReturnNullIfInputIsNull() {
        assertThat(transformer.toDto(null)).isNull();
    }

    @Test
    void toEntity_shouldReturnNullIfInputIsNull() {
        assertThat(transformer.toEntity(null)).isNull();
    }

    private static Address createSampleAddress(String firstName, String lastName, String country,
                                               String streetAddress, String city, String postCode) {
        return Address.builder()
                .firstName(firstName)
                .lastName(lastName)
                .country(country)
                .streetAddress(streetAddress)
                .city(city)
                .postCode(postCode)
                .build();
    }

    private static AddressDto createSampleAddressDto(String firstName, String lastName, String country,
                                                     String address, String city, String postCode) {
        return AddressDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .country(country)
                .address(address)
                .city(city)
                .postCode(postCode)
                .build();
    }

    // Helper methods for asserting

    private static void assertAddressDto(AddressDto dto, String firstName, String lastName, String country,
                                         String address, String city, String postCode) {
        assertThat(dto).isNotNull();
        assertThat(dto.getFirstName()).isEqualTo(firstName);
        assertThat(dto.getLastName()).isEqualTo(lastName);
        assertThat(dto.getCountry()).isEqualTo(country);
        assertThat(dto.getAddress()).isEqualTo(address);
        assertThat(dto.getCity()).isEqualTo(city);
        assertThat(dto.getPostCode()).isEqualTo(postCode);
    }

    private static void assertAddressEntity(Address entity, String firstName, String lastName, String country,
                                            String streetAddress, String city, String postCode) {
        assertThat(entity).isNotNull();
        assertThat(entity.getFirstName()).isEqualTo(firstName);
        assertThat(entity.getLastName()).isEqualTo(lastName);
        assertThat(entity.getCountry()).isEqualTo(country);
        assertThat(entity.getStreetAddress()).isEqualTo(streetAddress);
        assertThat(entity.getCity()).isEqualTo(city);
        assertThat(entity.getPostCode()).isEqualTo(postCode);
    }
}

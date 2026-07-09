package com.labaway.backend.unit.transformer.admin;

import com.labaway.backend.dto.admin.AdminUserCreateDto;
import com.labaway.backend.dto.admin.AdminUserDto;
import com.labaway.backend.entity.admin.AdminUser;
import com.labaway.backend.transformer.admin.AdminUserTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AdminUserTransformerTest {

    private AdminUserTransformer transformer;
    private final String username = "admin";
    private final String email = "admin@example.com";
    private final String password = "securePass";
    private final String role = "ADMIN";

    @BeforeEach
    void setUp() {
        transformer = new AdminUserTransformer();
    }

    @Test
    void toDto_shouldConvertEntityToDto() {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        AdminUser user = createAdminUser(id, now);
        AdminUserDto dto = transformer.toDto(user);

        assertDtoMatchesEntity(dto, id, now);
    }

    @Test
    void fromCreateDto_shouldConvertDtoToEntity() {
        AdminUserCreateDto dto = createAdminUserCreateDto();

        AdminUser entity = transformer.fromCreateDto(dto);

        assertEntityMatchesDto(entity, dto);
    }

    private AdminUser createAdminUser(UUID id, Instant timestamp) {
        return AdminUser.builder()
                .id(id)
                .username(username)
                .email(email)
                .role(role)
                .createdAt(timestamp)
                .updatedAt(timestamp)
                .build();
    }

    private AdminUserCreateDto createAdminUserCreateDto() {
        return new AdminUserCreateDto(
                username,
                email,
                password,
                role
        );
    }

    private void assertDtoMatchesEntity(AdminUserDto dto, UUID id, Instant timestamp) {
        LocalDateTime expected = timestamp.atZone(ZoneId.systemDefault()).toLocalDateTime();
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.username()).isEqualTo(username);
        assertThat(dto.email()).isEqualTo(email);
        assertThat(dto.role()).isEqualTo(role);
        assertThat(dto.createdAt()).isEqualTo(expected);
        assertThat(dto.updatedAt()).isEqualTo(expected);
    }

    private void assertEntityMatchesDto(AdminUser entity, AdminUserCreateDto dto) {
        assertThat(entity).isNotNull();
        assertThat(entity.getUsername()).isEqualTo(dto.username());
        assertThat(entity.getEmail()).isEqualTo(dto.email());
        assertThat(entity.getPassword()).isEqualTo(dto.password());
        assertThat(entity.getRole()).isEqualTo(dto.role());
    }
}

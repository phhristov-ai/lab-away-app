package com.labaway.backend.service;

import com.labaway.backend.dto.admin.AdminUserCreateDto;
import com.labaway.backend.dto.admin.AdminUserDto;
import com.labaway.backend.entity.AdminUser;
import com.labaway.backend.entity.repository.AdminUserRepository;
import com.labaway.backend.transformer.AdminUserTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminUserServiceTest {
    @Mock
    private AdminUserRepository adminUserRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AdminUserTransformer adminUserTransformer;
    @InjectMocks
    private AdminUserService adminUserService;
    private AdminUser adminUser;
    private AdminUserCreateDto createDto;
    private UUID userId;
    private final String email = "admin@example.com";
    private final String username = "admin";
    private final String rawPassword = "password123";
    private final String hashedPassword = "$2a$10$encoded";


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        adminUser = buildAdminUser();
        createDto = buildCreateDto();

        mockTransformerBehavior();
    }

    @Test
    void getAllAdminUsers_shouldReturnList() {
        when(adminUserRepository.findAll()).thenReturn(List.of(adminUser));

        List<AdminUserDto> result = adminUserService.getAllAdminUsers();

        assertEquals(1, result.size());
        assertEquals("admin", result.get(0).getUsername());
    }

    @Test
    void getAdminUserByUsername_shouldReturnDto() {
        mockFindByUsername(Optional.of(adminUser));
        AdminUserDto result = adminUserService.getAdminUserByUsername(username);

        assertEquals(userId, result.getId());
        assertEquals("admin@example.com", result.getEmail());
    }

    @Test
    void getAdminUserByUsername_shouldThrowIfNotFound() {
        mockFindByUsername(Optional.empty());
        assertThrows(RuntimeException.class, () -> adminUserService.getAdminUserByUsername(username));
    }


    @Test
    void createAdminUser_shouldHashPasswordAndReturnDto() {
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(adminUserRepository.save(any(AdminUser.class))).thenAnswer(inv -> {
            AdminUser u = inv.getArgument(0);
            u.setId(userId);
            u.setCreatedAt(Instant.now());
            u.setUpdatedAt(Instant.now());
            return u;
        });

        AdminUserDto result = adminUserService.createAdminUser(createDto);

        assertEquals("admin", result.getUsername());
        assertEquals("admin@example.com", result.getEmail());
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void deleteAdminUserByUsername_shouldCallRepository() {
        mockFindByUsername(Optional.of(adminUser));
        adminUserService.deleteAdminUserByUsername(username);
        verify(adminUserRepository).delete(adminUser);
    }


    @Test
    void validateCredentials_shouldReturnDto_whenPasswordMatches() {
        mockFindByUsername(Optional.of(adminUser));
        when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(true);

        Optional<AdminUserDto> result = adminUserService.validateCredentials(username, rawPassword);

        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
        assertEquals("admin", result.get().getUsername());
    }

    @Test
    void validateCredentials_shouldReturnEmpty_whenPasswordDoesNotMatch() {
        mockFindByUsername(Optional.of(adminUser));
        when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(false);

        Optional<AdminUserDto> result = adminUserService.validateCredentials(username, rawPassword);

        assertFalse(result.isPresent());
    }

    @Test
    void validateCredentials_shouldReturnEmpty_whenEmailNotFound() {
        mockFindByUsername(Optional.of(adminUser));
        Optional<AdminUserDto> result = adminUserService.validateCredentials(username, rawPassword);

        assertFalse(result.isPresent());
    }

    private AdminUser buildAdminUser() {
        return AdminUser.builder()
                .id(userId)
                .username(username)
                .email(email)
                .password(hashedPassword)
                .role("ADMIN")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    private AdminUserCreateDto buildCreateDto() {
        return AdminUserCreateDto.builder()
                .username(username)
                .email(email)
                .password(rawPassword)
                .role("ADMIN")
                .build();
    }

    private void mockTransformerBehavior() {
        when(adminUserTransformer.fromCreateDto(any())).thenAnswer(invocation -> {
            AdminUserCreateDto dto = invocation.getArgument(0);
            return AdminUser.builder()
                    .username(dto.getUsername())
                    .email(dto.getEmail())
                    .role(dto.getRole())
                    .build();
        });

        when(adminUserTransformer.toDto(any())).thenAnswer(invocation -> {
            AdminUser user = invocation.getArgument(0);
            return AdminUserDto.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .build();
        });
    }

    private void mockFindByUsername(Optional<AdminUser> userOpt) {
        when(adminUserRepository.findByUsername(username)).thenReturn(userOpt);
    }

}

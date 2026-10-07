package com.labaway.backend.unit.controller.admin;

import com.labaway.backend.controller.admin.AdminLoginController;
import com.labaway.backend.dto.error.ErrorResponse;
import com.labaway.backend.dto.security.JwtResponse;
import com.labaway.backend.security.JwtUtil;
import com.labaway.backend.dto.admin.AdminLoginRequest;
import com.labaway.backend.dto.admin.AdminUserDto;
import com.labaway.backend.service.admin.AdminUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminLoginControllerTest {

    @Mock
    private AdminUserService adminUserService;
    @InjectMocks
    private AdminLoginController adminLoginController;
    @Mock
    private JwtUtil jwtUtil;
    private final String email = "admin@example.com";
    private final String password = "password123";
    private final String username = "admin";
    private AdminUserDto mockAdminDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        adminLoginController = new AdminLoginController(adminUserService, jwtUtil);

        mockAdminDto = buildMockAdminUserDto();
    }

    @Test
    void login_shouldReturnOkResponse_whenCredentialsAreValid() {
        AdminLoginRequest request = buildLoginRequest(username, password);
        when(adminUserService.validateCredentials(username, password)).thenReturn(Optional.of(mockAdminDto));
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("mock-token");

        ResponseEntity<?> response = adminLoginController.login(request);

        verifySuccessfulLoginResponse(response);

        verify(adminUserService).validateCredentials(username, password);
        verify(jwtUtil).generateToken(mockAdminDto.username(), mockAdminDto.role());
    }

    @Test
    void login_shouldReturnUnauthorized_whenCredentialsAreInvalid() {
        AdminLoginRequest request = buildLoginRequest(username, "wrongPassword");
        when(adminUserService.validateCredentials(username, "wrongPassword")).thenReturn(Optional.empty());

        ResponseEntity<?> response = adminLoginController.login(request);

        verifyUnauthorizedLoginResponse(response);

        verify(adminUserService).validateCredentials(username, "wrongPassword");
    }

    private AdminLoginRequest buildLoginRequest(String username, String password) {
        return new AdminLoginRequest(username, password);
    }

    private AdminUserDto buildMockAdminUserDto() {
        return new AdminUserDto(
                UUID.randomUUID(),
                "admin",
                email,
                "ADMIN",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private void verifySuccessfulLoginResponse(ResponseEntity<?> response) {
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertNotNull(jwtResponse);
        assertEquals("mock-token", jwtResponse.token());
    }

    private void verifyUnauthorizedLoginResponse(ResponseEntity<?> response) {
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("Invalid credentials", errorResponse.message());
    }
}
package com.labaway.backend.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;
import com.labaway.backend.dto.admin.AdminUserCreateDto;
import com.labaway.backend.dto.admin.AdminUserDto;
import com.labaway.backend.service.AdminUserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

class AdminUserControllerTest {
    @Mock
    private AdminUserService adminUserService;

    @InjectMocks
    private AdminUserController adminUserController;

    private AdminUserDto sampleUserDto;
    private AdminUserCreateDto sampleCreateDto;
    private final String username = "admin";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sampleUserDto = buildSampleUserDto(username);
        sampleCreateDto = buildSampleCreateDto(username);
    }

    @Test
    void getAll_shouldReturnListOfUsers() {
        List<AdminUserDto> userList = List.of(sampleUserDto);
        when(adminUserService.getAllAdminUsers()).thenReturn(userList);

        List<AdminUserDto> result = adminUserController.getAll();

        assertEquals(userList, result);
        verify(adminUserService).getAllAdminUsers();
    }

    @Test
    void getByUsername_shouldReturnUserDto() {
        when(adminUserService.getAdminUserByUsername(username)).thenReturn(sampleUserDto);

        ResponseEntity<AdminUserDto> response = adminUserController.getByUsername(username);

        verifyUserDtoResponse(response, sampleUserDto, OK);
        verify(adminUserService).getAdminUserByUsername(username);
    }

    @Test
    void create_shouldReturnCreatedUser() {
        when(adminUserService.createAdminUser(sampleCreateDto)).thenReturn(sampleUserDto);

        ResponseEntity<AdminUserDto> response = adminUserController.create(sampleCreateDto);

        verifyUserDtoResponse(response, sampleUserDto, CREATED);
        verify(adminUserService).createAdminUser(sampleCreateDto);
    }

    @Test
    void update_shouldReturnUpdatedUser() {
        when(adminUserService.updateAdminUserByUsername(username, sampleCreateDto)).thenReturn(sampleUserDto);

        ResponseEntity<AdminUserDto> response = adminUserController.update(username, sampleCreateDto);

        verifyUserDtoResponse(response, sampleUserDto, OK);
        verify(adminUserService).updateAdminUserByUsername(username, sampleCreateDto);
    }

    @Test
    void delete_shouldCallDeleteAndReturnNoContent() {
        doNothing().when(adminUserService).deleteAdminUserByUsername(username);

        ResponseEntity<Void> response = adminUserController.delete(username);

        assertEquals(NO_CONTENT, response.getStatusCode());
        verify(adminUserService).deleteAdminUserByUsername(username);
    }

    // Helper Methods

    private AdminUserDto buildSampleUserDto(String username) {
        return AdminUserDto.builder()
                .username(username)
                .email("admin@example.com")
                .role("ADMIN")
                .build();
    }

    private AdminUserCreateDto buildSampleCreateDto(String username) {
        return AdminUserCreateDto.builder()
                .username(username)
                .email("admin@example.com")
                .password("password")
                .role("ADMIN")
                .build();
    }

    private void verifyUserDtoResponse(ResponseEntity<AdminUserDto> response, AdminUserDto expectedDto, HttpStatus expectedStatus) {
        assertEquals(expectedStatus, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
    }
}
package com.labaway.backend.controller;

import com.labaway.backend.dto.admin.AdminUserCreateDto;
import com.labaway.backend.dto.admin.AdminUserDto;
import com.labaway.backend.service.admin.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin-users")
@RequiredArgsConstructor
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<AdminUserDto> getAll() {
        return adminUserService.getAllAdminUsers();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{username}")
    public ResponseEntity<AdminUserDto> getByUsername(@PathVariable String username) {
        return ResponseEntity.ok(adminUserService.getAdminUserByUsername(username));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<AdminUserDto> create(@RequestBody AdminUserCreateDto dto) {
        return new ResponseEntity<>(adminUserService.createAdminUser(dto), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{username}")
    public ResponseEntity<AdminUserDto> update(@PathVariable String username, @RequestBody AdminUserCreateDto dto) {
        return ResponseEntity.ok(adminUserService.updateAdminUserByUsername(username, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> delete(@PathVariable String username) {
        adminUserService.deleteAdminUserByUsername(username);
        return ResponseEntity.noContent().build();
    }
}

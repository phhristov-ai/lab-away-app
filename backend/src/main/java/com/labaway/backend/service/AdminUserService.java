package com.labaway.backend.service;

import com.labaway.backend.dto.admin.AdminUserCreateDto;
import com.labaway.backend.dto.admin.AdminUserDto;
import com.labaway.backend.entity.AdminUser;
import com.labaway.backend.entity.repository.AdminUserRepository;
import com.labaway.backend.transformer.AdminUserTransformer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminUserTransformer adminUserTransformer;

    public List<AdminUserDto> getAllAdminUsers() {
        return adminUserRepository.findAll().stream()
                .map(adminUserTransformer::toDto)
                .toList();
    }

    public AdminUserDto getAdminUserByUsername(String username) {
        return adminUserRepository.findByUsername(username)
                .map(adminUserTransformer::toDto)
                .orElseThrow(() -> new RuntimeException("Admin user not found"));
    }

    public AdminUserDto createAdminUser(AdminUserCreateDto dto) {
        AdminUser adminUser = adminUserTransformer.fromCreateDto(dto);
        adminUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        adminUser = adminUserRepository.save(adminUser);
        return adminUserTransformer.toDto(adminUser);
    }

    public AdminUserDto updateAdminUserByUsername(String username, AdminUserCreateDto dto) {
        AdminUser user = adminUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());

        return adminUserTransformer.toDto(adminUserRepository.save(user));
    }

    public void deleteAdminUserByUsername(String username) {
        AdminUser user = adminUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        adminUserRepository.delete(user);
    }

    public Optional<AdminUserDto> validateCredentials(String username, String rawPassword) {
        return adminUserRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPassword()))
                .map(adminUserTransformer::toDto);
    }
}

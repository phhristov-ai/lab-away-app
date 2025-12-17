package com.labaway.backend.transformer;

import com.labaway.backend.dto.admin.AdminUserCreateDto;
import com.labaway.backend.dto.admin.AdminUserDto;
import com.labaway.backend.entity.admin.AdminUser;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
public class AdminUserTransformer {

    public AdminUserDto toDto(AdminUser user) {
        return AdminUserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .updatedAt(user.getUpdatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .build();
    }

    public AdminUser fromCreateDto(AdminUserCreateDto dto) {
        AdminUser user = new AdminUser();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        return user;
    }
}
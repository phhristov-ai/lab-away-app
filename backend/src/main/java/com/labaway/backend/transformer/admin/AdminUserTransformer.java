package com.labaway.backend.transformer.admin;

import com.labaway.backend.dto.admin.AdminUserCreateDto;
import com.labaway.backend.dto.admin.AdminUserDto;
import com.labaway.backend.entity.admin.AdminUser;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
public class AdminUserTransformer {

    public AdminUserDto toDto(AdminUser user) {
        return new AdminUserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                user.getUpdatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime()
        );
    }

    public AdminUser fromCreateDto(AdminUserCreateDto dto) {
        AdminUser user = new AdminUser();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setRole(dto.role());
        return user;
    }
}
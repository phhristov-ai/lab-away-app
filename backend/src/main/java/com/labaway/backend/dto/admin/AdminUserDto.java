package com.labaway.backend.dto.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminUserDto(
        @NotNull
        UUID id,

        @NotBlank(message = "Username is required")
        @Size(min = 5, max = 255, message = "Username must be between 5 and 255 characters")
        String username,

        @Email(message = "Email should be valid")
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Role is required")
        String role,

        @NotNull
        LocalDateTime createdAt,

        @NotNull
        LocalDateTime updatedAt
) {}
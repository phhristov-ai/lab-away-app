package com.labaway.backend.dto.admin;

import jakarta.validation.constraints.*;

public record AdminUserCreateDto(

        @NotBlank(message = "Username is required")
        @Size(min = 5, max = 255, message = "Username must be between 5 and 255 characters")
        String username,

        @Email(message = "Email should be valid")
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        @NotBlank(message = "Role is required")
        String role

) {}
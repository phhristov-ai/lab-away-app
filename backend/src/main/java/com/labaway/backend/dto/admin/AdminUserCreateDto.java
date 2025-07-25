package com.labaway.backend.dto.admin;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminUserCreateDto {

    @NotBlank(message = "Username is required")
    @Size(min = 5, max = 255, message = "Username must be between 5 and 255 characters")
    private String username;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Role is required")
    private String role;
}

package com.labaway.backend.dto.admin;

public record AdminLoginRequest(
        String username,
        String password
) {
}

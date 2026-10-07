package com.labaway.backend.controller.admin;

import com.labaway.backend.dto.error.ErrorResponse;
import com.labaway.backend.dto.security.JwtResponse;
import com.labaway.backend.security.JwtUtil;
import com.labaway.backend.dto.admin.AdminLoginRequest;
import com.labaway.backend.dto.admin.AdminUserDto;
import com.labaway.backend.service.admin.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/admin-login")
@RequiredArgsConstructor
public class AdminLoginController {

    private final AdminUserService adminUserService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<Object> login(@RequestBody AdminLoginRequest request) {
        Optional<AdminUserDto> adminOpt = adminUserService.validateCredentials(request.username(), request.password());

        if (adminOpt.isPresent()) {
            AdminUserDto admin = adminOpt.get();
            String token = jwtUtil.generateToken(admin.username(), admin.role());
            return ResponseEntity.ok(new JwtResponse(token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid credentials"));
        }
    }

}

package com.stis.alumni.controller;

import com.stis.alumni.dto.ApiResponse;
import com.stis.alumni.dto.auth.AuthLoginRequest;
import com.stis.alumni.dto.auth.AuthLoginResponse;
import com.stis.alumni.dto.auth.AuthRegisterRequest;
import com.stis.alumni.dto.auth.AuthRegisterResponse;
import com.stis.alumni.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autentikasi")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrasi pengguna baru", security = {})
    public ResponseEntity<ApiResponse<AuthRegisterResponse>> register(@Valid @RequestBody AuthRegisterRequest request) {
        AuthRegisterResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful", response));
    }

    @PostMapping("/login")
    @Operation(summary = "Masuk dan mendapatkan token JWT", security = {})
    public ResponseEntity<ApiResponse<AuthLoginResponse>> login(@Valid @RequestBody AuthLoginRequest request) {
        AuthLoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
}

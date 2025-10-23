package com.stis.alumni.controller;

import com.stis.alumni.dto.ApiResponse;
import com.stis.alumni.dto.user.AccountDeletionRequest;
import com.stis.alumni.dto.user.ChangePasswordRequest;
import com.stis.alumni.dto.user.UserProfileResponse;
import com.stis.alumni.dto.user.UserUpdateRequest;
import com.stis.alumni.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Profil Pengguna")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    @Operation(summary = "Menampilkan profil pengguna yang sedang masuk")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile() {
        return ResponseEntity.ok(ApiResponse.success(userService.getCurrentProfile()));
    }

    @PutMapping("/profile")
    @Operation(summary = "Memperbarui profil pengguna yang sedang masuk")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(@Valid @RequestBody UserUpdateRequest request) {
        UserProfileResponse response = userService.updateProfile(request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", response));
    }

    @PutMapping("/change-password")
    @Operation(summary = "Mengganti kata sandi pengguna yang sedang masuk")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }

    @DeleteMapping("/account")
    @Operation(summary = "Menonaktifkan akun pengguna yang sedang masuk")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@Valid @RequestBody AccountDeletionRequest request) {
        userService.deleteAccount(request);
        return ResponseEntity.ok(ApiResponse.success("Account deleted successfully", null));
    }
}

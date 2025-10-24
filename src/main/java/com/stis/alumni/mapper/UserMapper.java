package com.stis.alumni.mapper;

import com.stis.alumni.dto.auth.AuthRegisterRequest;
import com.stis.alumni.dto.auth.AuthRegisterResponse;
import com.stis.alumni.dto.user.UserDetailResponse;
import com.stis.alumni.dto.user.UserListItemResponse;
import com.stis.alumni.dto.user.UserProfileResponse;
import com.stis.alumni.dto.user.UserSummaryResponse;
import com.stis.alumni.dto.user.UserUpdateRequest;
import com.stis.alumni.entity.User;
import com.stis.alumni.enums.UserRole;
import com.stis.alumni.enums.UserStatus;

import java.util.Optional;

public final class UserMapper {

    private UserMapper() {
    }

    public static User toEntity(AuthRegisterRequest request, String encodedPassword) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(encodedPassword);
        user.setFullName(request.getFullName());
        user.setNim(normalizeTrimmed(request.getNim()));
        user.setAngkatan(request.getAngkatan());
        user.setProgramStudi(normalizeProgramStudi(request.getProgramStudi()));
        user.setJurusan(normalizeJurusan(request.getJurusan()));
        user.setTahunLulus(request.getTahunLulus());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAlamat(request.getAlamat());
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }

    public static void applyUpdate(User user, UserUpdateRequest request) {
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getProgramStudi() != null) {
            user.setProgramStudi(normalizeProgramStudi(request.getProgramStudi()));
        }
        if (request.getJurusan() != null) {
            user.setJurusan(normalizeJurusan(request.getJurusan()));
        }
        if (request.getTahunLulus() != null) {
            user.setTahunLulus(request.getTahunLulus());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getAlamat() != null) {
            user.setAlamat(request.getAlamat());
        }
    }

    public static UserSummaryResponse toSummary(User user) {
        if (user == null) {
            return null;
        }
        return new UserSummaryResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRole()
        );
    }

    public static UserDetailResponse toDetail(User user) {
        if (user == null) {
            return null;
        }
        UserDetailResponse response = new UserDetailResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setNim(user.getNim());
        response.setAngkatan(user.getAngkatan());
        response.setProgramStudi(user.getProgramStudi());
        response.setJurusan(user.getJurusan());
        response.setTahunLulus(user.getTahunLulus());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setAlamat(user.getAlamat());
        response.setStatus(user.getStatus());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }

    public static AuthRegisterResponse toRegisterResponse(User user) {
        if (user == null) {
            return null;
        }
        AuthRegisterResponse response = new AuthRegisterResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setNim(user.getNim());
        response.setAngkatan(user.getAngkatan());
        response.setProgramStudi(user.getProgramStudi());
        response.setJurusan(user.getJurusan());
        response.setTahunLulus(user.getTahunLulus());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }

    public static UserProfileResponse toProfile(User user) {
        if (user == null) {
            return null;
        }
        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setNim(user.getNim());
        response.setAngkatan(user.getAngkatan());
        response.setProgramStudi(user.getProgramStudi());
        response.setJurusan(user.getJurusan());
        response.setTahunLulus(user.getTahunLulus());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setAlamat(user.getAlamat());
        response.setStatus(user.getStatus());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        response.setWorkHistories(WorkHistoryMapper.toResponses(user.getWorkHistories()));
        return response;
    }

    public static UserListItemResponse toListItem(User user) {
        if (user == null) {
            return null;
        }
        UserListItemResponse response = new UserListItemResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setNim(user.getNim());
        response.setAngkatan(user.getAngkatan());
        response.setProgramStudi(user.getProgramStudi());
        response.setJurusan(user.getJurusan());
        response.setStatus(user.getStatus());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }

    public static void updateRole(User user, UserRole role) {
        Optional.ofNullable(role).ifPresent(user::setRole);
    }

    public static void updateStatus(User user, UserStatus status) {
        Optional.ofNullable(status).ifPresent(user::setStatus);
    }

    private static String normalizeProgramStudi(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }

    private static String normalizeJurusan(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim().toLowerCase();
        return switch (normalized) {
            case "d4 komputasi statistik" -> "D4 komputasi statistik";
            case "d4 statistik" -> "D4 statistik";
            case "d3 statistik" -> "D3 statistik";
            default -> value.trim();
        };
    }

    private static String normalizeTrimmed(String value) {
        return value == null ? null : value.trim();
    }
}

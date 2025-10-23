package com.stis.alumni.controller;

import com.stis.alumni.dto.ApiResponse;
import com.stis.alumni.dto.PageResponse;
import com.stis.alumni.dto.user.UserListItemResponse;
import com.stis.alumni.dto.user.UserProfileResponse;
import com.stis.alumni.dto.user.UserSearchCriteria;
import com.stis.alumni.enums.UserRole;
import com.stis.alumni.enums.UserStatus;
import com.stis.alumni.exception.BadRequestException;
import com.stis.alumni.service.AlumniService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alumni")
@Tag(name = "Manajemen Alumni")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AlumniController {

    private final AlumniService alumniService;

    public AlumniController(AlumniService alumniService) {
        this.alumniService = alumniService;
    }

    @GetMapping
    @Operation(summary = "Menampilkan daftar alumni dengan filter dan paginasi")
    public ApiResponse<PageResponse<UserListItemResponse>> getAlumni(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fullName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) Integer angkatan,
            @RequestParam(required = false) String programStudi,
            @RequestParam(required = false) Integer tahunLulus,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String search
    ) {
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setAngkatan(angkatan);
        criteria.setProgramStudi(programStudi);
        criteria.setTahunLulus(tahunLulus);
        criteria.setSearch(search);
        if (StringUtils.hasText(status)) {
            try {
                criteria.setStatus(UserStatus.valueOf(status.toUpperCase()));
            } catch (IllegalArgumentException ex) {
                throw new BadRequestException("Invalid status value: " + status);
            }
        }
        if (StringUtils.hasText(role)) {
            try {
                criteria.setRole(UserRole.valueOf(role.toUpperCase()));
            } catch (IllegalArgumentException ex) {
                throw new BadRequestException("Invalid role value: " + role);
            }
        }
        PageResponse<UserListItemResponse> response = alumniService.getAlumniList(page, size, sortBy, sortDirection, criteria);
        return ApiResponse.success(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Menampilkan detail alumni berdasarkan ID")
    public ApiResponse<UserProfileResponse> getAlumniDetail(@PathVariable Long id) {
        return ApiResponse.success(alumniService.getAlumniDetail(id));
    }
}

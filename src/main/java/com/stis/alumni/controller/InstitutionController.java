package com.stis.alumni.controller;

import com.stis.alumni.dto.ApiResponse;
import com.stis.alumni.dto.OptionResponse;
import com.stis.alumni.dto.PageResponse;
import com.stis.alumni.dto.institution.InstitutionDetailResponse;
import com.stis.alumni.dto.institution.InstitutionListItemResponse;
import com.stis.alumni.dto.institution.InstitutionRequest;
import com.stis.alumni.dto.institution.InstitutionResponse;
import com.stis.alumni.dto.institution.InstitutionSearchCriteria;
import com.stis.alumni.dto.institution.InstitutionUpdateRequest;
import com.stis.alumni.enums.InstitutionType;
import com.stis.alumni.exception.BadRequestException;
import com.stis.alumni.service.InstitutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/institutions")
@Tag(name = "Manajemen Institusi")
@SecurityRequirement(name = "bearerAuth")
public class InstitutionController {

    private final InstitutionService institutionService;

    public InstitutionController(InstitutionService institutionService) {
        this.institutionService = institutionService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Menambahkan institusi baru")
    public ResponseEntity<ApiResponse<InstitutionResponse>> create(@Valid @RequestBody InstitutionRequest request) {
        InstitutionResponse response = institutionService.createInstitution(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Institution created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Menampilkan daftar institusi")
    public ApiResponse<PageResponse<InstitutionListItemResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String city
    ) {
        InstitutionSearchCriteria criteria = new InstitutionSearchCriteria();
        criteria.setSearch(search);
        criteria.setProvince(province);
        criteria.setCity(city);
        if (StringUtils.hasText(type)) {
            try {
                criteria.setType(InstitutionType.valueOf(type.toUpperCase()));
            } catch (IllegalArgumentException ex) {
                throw new BadRequestException("Invalid institution type: " + type);
            }
        }
        PageResponse<InstitutionListItemResponse> response = institutionService.getInstitutions(page, size, sortBy, sortDirection, criteria);
        return ApiResponse.success(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Menampilkan detail institusi")
    public ApiResponse<InstitutionDetailResponse> detail(@PathVariable Long id) {
        return ApiResponse.success(institutionService.getInstitutionDetail(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Memperbarui institusi")
    public ApiResponse<InstitutionResponse> update(@PathVariable Long id, @RequestBody InstitutionUpdateRequest request) {
        return ApiResponse.success("Institution updated successfully", institutionService.updateInstitution(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Menghapus institusi")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        institutionService.deleteInstitution(id);
        return ApiResponse.success("Institution deleted successfully", null);
    }

    @GetMapping("/options")
    @Operation(summary = "Menampilkan opsi institusi untuk dropdown")
    public ApiResponse<List<OptionResponse>> options(@RequestParam(required = false) String search) {
        return ApiResponse.success(institutionService.getInstitutionOptions(search));
    }
}

package com.stis.alumni.controller;

import com.stis.alumni.dto.ApiResponse;
import com.stis.alumni.dto.workhistory.WorkHistoryRequest;
import com.stis.alumni.dto.workhistory.WorkHistoryResponse;
import com.stis.alumni.dto.workhistory.WorkHistoryUpdateRequest;
import com.stis.alumni.service.WorkHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/work-histories")
@Tag(name = "Riwayat Pekerjaan")
@SecurityRequirement(name = "bearerAuth")
public class WorkHistoryController {

    private final WorkHistoryService workHistoryService;

    public WorkHistoryController(WorkHistoryService workHistoryService) {
        this.workHistoryService = workHistoryService;
    }

    @PostMapping
    @Operation(summary = "Menambahkan riwayat pekerjaan baru")
    public ResponseEntity<ApiResponse<WorkHistoryResponse>> create(@Valid @RequestBody WorkHistoryRequest request) {
        WorkHistoryResponse response = workHistoryService.createWorkHistory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Work history created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Menampilkan daftar riwayat pekerjaan pengguna")
    public ApiResponse<List<WorkHistoryResponse>> list() {
        return ApiResponse.success(workHistoryService.getCurrentUserWorkHistories());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Menampilkan detail riwayat pekerjaan")
    public ApiResponse<WorkHistoryResponse> detail(@PathVariable Long id) {
        return ApiResponse.success(workHistoryService.getWorkHistory(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Memperbarui riwayat pekerjaan")
    public ApiResponse<WorkHistoryResponse> update(@PathVariable Long id, @RequestBody WorkHistoryUpdateRequest request) {
        return ApiResponse.success("Work history updated successfully", workHistoryService.updateWorkHistory(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Menghapus riwayat pekerjaan")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        workHistoryService.deleteWorkHistory(id);
        return ApiResponse.success("Work history deleted successfully", null);
    }
}

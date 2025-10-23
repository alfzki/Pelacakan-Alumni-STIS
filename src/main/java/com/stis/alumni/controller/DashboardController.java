package com.stis.alumni.controller;

import com.stis.alumni.dto.ApiResponse;
import com.stis.alumni.dto.dashboard.DashboardSummaryResponse;
import com.stis.alumni.dto.dashboard.StatisticItemResponse;
import com.stis.alumni.dto.dashboard.TopInstitutionResponse;
import com.stis.alumni.dto.dashboard.TopLocationResponse;
import com.stis.alumni.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dasbor")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final StatisticsService statisticsService;

    public DashboardController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/summary")
    @Operation(summary = "Mengambil ringkasan metrik dasbor")
    public ApiResponse<DashboardSummaryResponse> summary() {
        return ApiResponse.success(statisticsService.getDashboardSummary());
    }

    @GetMapping("/employment-distribution")
    @Operation(summary = "Mengambil distribusi jenis pekerjaan")
    public ApiResponse<List<StatisticItemResponse>> employmentDistribution() {
        return ApiResponse.success(statisticsService.getEmploymentDistribution());
    }

    @GetMapping("/institution-type-distribution")
    @Operation(summary = "Mengambil distribusi tipe institusi")
    public ApiResponse<List<StatisticItemResponse>> institutionTypeDistribution() {
        return ApiResponse.success(statisticsService.getInstitutionTypeDistribution());
    }

    @GetMapping("/top-institutions")
    @Operation(summary = "Mengambil daftar institusi dengan alumni terbanyak")
    public ApiResponse<List<TopInstitutionResponse>> topInstitutions(@RequestParam(defaultValue = "5") int limit) {
        return ApiResponse.success(statisticsService.getTopInstitutions(limit));
    }

    @GetMapping("/top-locations")
    @Operation(summary = "Mengambil lokasi dengan alumni terbanyak")
    public ApiResponse<List<TopLocationResponse>> topLocations(@RequestParam(defaultValue = "5") int limit) {
        return ApiResponse.success(statisticsService.getTopLocations(limit));
    }

    @GetMapping("/angkatan-distribution")
    @Operation(summary = "Mengambil distribusi berdasarkan angkatan")
    public ApiResponse<List<StatisticItemResponse>> angkatanDistribution() {
        return ApiResponse.success(statisticsService.getAngkatanDistribution());
    }

    @GetMapping("/working-status")
    @Operation(summary = "Mengambil distribusi status bekerja")
    public ApiResponse<List<StatisticItemResponse>> workingStatus() {
        return ApiResponse.success(statisticsService.getWorkingStatusDistribution());
    }
}

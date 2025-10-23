package com.stis.alumni.controller;

import com.stis.alumni.dto.ApiResponse;
import com.stis.alumni.dto.dashboard.StatisticItemResponse;
import com.stis.alumni.dto.statistics.StatisticsOverviewResponse;
import com.stis.alumni.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@Tag(name = "Statistik")
@SecurityRequirement(name = "bearerAuth")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/overview")
    @Operation(summary = "Mengambil ringkasan statistik")
    public ApiResponse<StatisticsOverviewResponse> overview() {
        return ApiResponse.success(statisticsService.getStatisticsOverview());
    }

    @GetMapping("/alumni-distribution")
    @Operation(summary = "Mengambil distribusi alumni berdasarkan provinsi, institusi, atau tipe")
    public ApiResponse<List<StatisticItemResponse>> alumniDistribution(
            @Parameter(description = "Parameter pengelompokan", schema = @Schema(allowableValues = {"province", "institution", "type"}))
            @RequestParam String groupBy) {
        return ApiResponse.success(statisticsService.getAlumniDistribution(groupBy));
    }
}

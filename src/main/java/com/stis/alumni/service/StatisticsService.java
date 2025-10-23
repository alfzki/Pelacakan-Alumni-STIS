package com.stis.alumni.service;

import com.stis.alumni.dto.dashboard.DashboardSummaryResponse;
import com.stis.alumni.dto.dashboard.StatisticItemResponse;
import com.stis.alumni.dto.dashboard.TopInstitutionResponse;
import com.stis.alumni.dto.dashboard.TopLocationResponse;
import com.stis.alumni.dto.statistics.StatisticsOverviewResponse;
import com.stis.alumni.entity.Institution;
import com.stis.alumni.enums.EmploymentType;
import com.stis.alumni.enums.InstitutionType;
import com.stis.alumni.enums.UserStatus;
import com.stis.alumni.exception.BadRequestException;
import com.stis.alumni.repository.InstitutionRepository;
import com.stis.alumni.repository.UserRepository;
import com.stis.alumni.repository.WorkHistoryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class StatisticsService {

    private final UserRepository userRepository;
    private final InstitutionRepository institutionRepository;
    private final WorkHistoryRepository workHistoryRepository;

    public StatisticsService(UserRepository userRepository,
                             InstitutionRepository institutionRepository,
                             WorkHistoryRepository workHistoryRepository) {
        this.userRepository = userRepository;
        this.institutionRepository = institutionRepository;
        this.workHistoryRepository = workHistoryRepository;
    }

    public DashboardSummaryResponse getDashboardSummary() {
        long totalAlumni = userRepository.count();
        long activeAlumni = userRepository.countByStatus(UserStatus.ACTIVE);
        long inactiveAlumni = userRepository.countByStatus(UserStatus.INACTIVE);
        long workingAlumni = workHistoryRepository.countDistinctUserWithCurrentJob();
        long nonWorkingAlumni = Math.max(totalAlumni - workingAlumni, 0);
        long totalInstitutions = institutionRepository.count();
        return new DashboardSummaryResponse(totalAlumni, activeAlumni, inactiveAlumni, workingAlumni, nonWorkingAlumni, totalInstitutions);
    }

    public List<StatisticItemResponse> getEmploymentDistribution() {
        long total = workHistoryRepository.countDistinctUserWithCurrentJob();
        List<StatisticItemResponse> responses = new ArrayList<>();
        workHistoryRepository.countCurrentEmploymentDistribution().forEach(row -> {
            EmploymentType type = (EmploymentType) row[0];
            long count = ((Number) row[1]).longValue();
            responses.add(new StatisticItemResponse(type.name(), count, calculatePercentage(count, total)));
        });
        return responses;
    }

    public List<StatisticItemResponse> getInstitutionTypeDistribution() {
        long total = workHistoryRepository.countDistinctUserWithCurrentJob();
        List<StatisticItemResponse> responses = new ArrayList<>();
        workHistoryRepository.countCurrentInstitutionTypeDistribution().forEach(row -> {
            InstitutionType type = (InstitutionType) row[0];
            long count = ((Number) row[1]).longValue();
            responses.add(new StatisticItemResponse(type.name(), count, calculatePercentage(count, total)));
        });
        return responses;
    }

    public List<TopInstitutionResponse> getTopInstitutions(int limit) {
        return workHistoryRepository.findTopInstitutions(PageRequest.of(0, limit)).stream()
                .map(row -> {
                    Institution institution = (Institution) row[0];
                    long count = ((Number) row[1]).longValue();
                    return new TopInstitutionResponse(
                            institution.getId(),
                            institution.getName(),
                            institution.getType(),
                            institution.getProvince(),
                            institution.getCity(),
                            count
                    );
                })
                .toList();
    }

    public List<TopLocationResponse> getTopLocations(int limit) {
        return workHistoryRepository.findTopLocations(PageRequest.of(0, limit)).stream()
                .map(row -> {
                    String province = normalizeLabel((String) row[0]);
                    String city = normalizeLabel((String) row[1]);
                    long count = ((Number) row[2]).longValue();
                    return new TopLocationResponse(province, city, count);
                })
                .toList();
    }

    public List<StatisticItemResponse> getAngkatanDistribution() {
        long total = userRepository.count();
        List<StatisticItemResponse> responses = new ArrayList<>();
        userRepository.countByAngkatan().forEach(row -> {
            Integer angkatan = (Integer) row[0];
            long count = ((Number) row[1]).longValue();
            responses.add(new StatisticItemResponse(String.valueOf(angkatan), count, calculatePercentage(count, total)));
        });
        return responses;
    }

    public List<StatisticItemResponse> getWorkingStatusDistribution() {
        long working = workHistoryRepository.countDistinctUserWithCurrentJob();
        long total = userRepository.count();
        long nonWorking = Math.max(total - working, 0);
        List<StatisticItemResponse> responses = new ArrayList<>();
        responses.add(new StatisticItemResponse("WORKING", working, calculatePercentage(working, total)));
        responses.add(new StatisticItemResponse("NOT_WORKING", nonWorking, calculatePercentage(nonWorking, total)));
        return responses;
    }

    public StatisticsOverviewResponse getStatisticsOverview() {
        StatisticsOverviewResponse response = new StatisticsOverviewResponse();
        response.setTotalAlumni(userRepository.count());
        response.setTotalInstitutions(institutionRepository.count());

        Map<String, Long> byInstitutionType = new LinkedHashMap<>();
        workHistoryRepository.countCurrentInstitutionTypeDistribution()
                .forEach(row -> byInstitutionType.put(((InstitutionType) row[0]).name(), ((Number) row[1]).longValue()));
        response.setByInstitutionType(byInstitutionType);

        Map<String, Long> byAngkatan = new LinkedHashMap<>();
        userRepository.countByAngkatan()
                .forEach(row -> byAngkatan.put(String.valueOf(row[0]), ((Number) row[1]).longValue()));
        response.setByAngkatan(byAngkatan);

        Map<String, Long> byEmploymentType = new LinkedHashMap<>();
        workHistoryRepository.countCurrentEmploymentDistribution()
                .forEach(row -> byEmploymentType.put(((EmploymentType) row[0]).name(), ((Number) row[1]).longValue()));
        response.setByEmploymentType(byEmploymentType);

        return response;
    }

    public List<StatisticItemResponse> getAlumniDistribution(String groupBy) {
        if (!StringUtils.hasText(groupBy)) {
            throw new BadRequestException("groupBy is required");
        }
        long total = workHistoryRepository.countDistinctUserWithCurrentJob();
        if (total == 0) {
            return List.of();
        }
        List<StatisticItemResponse> responses = new ArrayList<>();
        switch (groupBy.toLowerCase(Locale.ROOT)) {
            case "province" -> workHistoryRepository.countCurrentByProvince()
                    .forEach(row -> {
                        String province = normalizeLabel((String) row[0]);
                        long count = ((Number) row[1]).longValue();
                        responses.add(new StatisticItemResponse(province, count, calculatePercentage(count, total)));
                    });
            case "institution" -> workHistoryRepository.countCurrentByInstitution()
                    .forEach(row -> {
                        String name = normalizeLabel((String) row[0]);
                        long count = ((Number) row[1]).longValue();
                        responses.add(new StatisticItemResponse(name, count, calculatePercentage(count, total)));
                    });
            case "type" -> workHistoryRepository.countCurrentInstitutionTypeDistribution()
                    .forEach(row -> {
                        InstitutionType type = (InstitutionType) row[0];
                        long count = ((Number) row[1]).longValue();
                        responses.add(new StatisticItemResponse(type.name(), count, calculatePercentage(count, total)));
                    });
            default -> throw new BadRequestException("Invalid groupBy value: " + groupBy);
        }
        return responses;
    }

    private double calculatePercentage(long count, long total) {
        if (total <= 0) {
            return 0.0;
        }
        return Math.round(((double) count / total) * 10000.0) / 100.0;
    }

    private String normalizeLabel(String value) {
        return value == null || value.isBlank() ? "UNKNOWN" : value;
    }
}

package com.stis.alumni.integration;

import com.stis.alumni.enums.InstitutionType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatisticsTests extends BaseIntegrationTest {

    @Test
    void overviewShouldProvideAggregatedStatistics() {
        Map<String, Object> overview = givenAuth(adminToken)
                .when()
                .get("/api/statistics/overview")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .extract()
                .path("data");

        assertEquals(Math.toIntExact(userRepository.count()), ((Number) overview.get("totalAlumni")).intValue());
        assertEquals(Math.toIntExact(institutionRepository.count()), ((Number) overview.get("totalInstitutions")).intValue());

        @SuppressWarnings("unchecked")
        Map<String, Object> typeBreakdown = (Map<String, Object>) overview.get("byInstitutionType");
        Map<String, Integer> expectedDistribution = workHistoryRepository.countCurrentInstitutionTypeDistribution().stream()
                .collect(Collectors.toMap(
                        row -> ((InstitutionType) row[0]).name(),
                        row -> ((Number) row[1]).intValue(),
                        Integer::sum
                ));

        assertTrue(expectedDistribution.containsKey("BPS_KOTA_KABUPATEN"));
        assertTrue(expectedDistribution.containsKey("SWASTA"));
        assertTrue(typeBreakdown.containsKey("BPS_KOTA_KABUPATEN"));
        assertTrue(typeBreakdown.containsKey("SWASTA"));
        assertEquals(expectedDistribution.get("BPS_KOTA_KABUPATEN"),
                ((Number) typeBreakdown.get("BPS_KOTA_KABUPATEN")).intValue());
        assertEquals(expectedDistribution.get("SWASTA"),
                ((Number) typeBreakdown.get("SWASTA")).intValue());
    }

    @Test
    void alumniDistributionByProvinceShouldReturnValues() {
        List<String> labels = givenAuth(adminToken)
                .when()
                .get("/api/statistics/alumni-distribution?groupBy=province")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .extract()
                .path("data.label");

        assertTrue(labels.contains("DKI Jakarta"));
        assertTrue(labels.contains("Jawa Barat"));
    }

    @Test
    void alumniDistributionByInstitutionShouldReturnValues() {
        List<String> labels = givenAuth(adminToken)
                .when()
                .get("/api/statistics/alumni-distribution?groupBy=institution")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .extract()
                .path("data.label");

        assertTrue(labels.contains(primaryInstitution.getName()));
        assertTrue(labels.contains(secondaryInstitution.getName()));
    }

    @Test
    void alumniDistributionByTypeShouldReturnValues() {
        List<String> labels = givenAuth(adminToken)
                .when()
                .get("/api/statistics/alumni-distribution?groupBy=type")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .extract()
                .path("data.label");

        assertTrue(labels.contains("BPS_KOTA_KABUPATEN"));
        assertTrue(labels.contains("SWASTA"));
    }

    @Test
    void alumniDistributionWithInvalidGroupByShouldReturnBadRequest() {
        givenAuth(adminToken)
                .when()
                .get("/api/statistics/alumni-distribution?groupBy=invalid")
                .then()
                .statusCode(400)
                .body("status", equalTo("error"))
                .body("message", notNullValue());
    }
}

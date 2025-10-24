package com.stis.alumni.integration;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DashboardTests extends BaseIntegrationTest {

    @Test
    void summaryShouldReturnAggregateCounts() {
        long totalAlumni = userRepository.count();
        long totalInstitutions = institutionRepository.count();

        Map<String, Object> summary = givenAuth(adminToken)
                .when()
                .get("/api/dashboard/summary")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .extract()
                .path("data");

        assertEquals(Math.toIntExact(totalAlumni), ((Number) summary.get("totalAlumni")).intValue());
        assertEquals(Math.toIntExact(totalInstitutions), ((Number) summary.get("totalInstitutions")).intValue());
    }

    @Test
    void employmentDistributionShouldListCurrentEmploymentTypes() {
        List<Map<String, Object>> distribution = givenAuth(adminToken)
                .when()
                .get("/api/dashboard/employment-distribution")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .extract()
                .path("data");

        Map<String, Integer> counts = distribution.stream()
                .collect(Collectors.toMap(
                        entry -> entry.get("label").toString(),
                        entry -> ((Number) entry.get("count")).intValue(),
                        Integer::sum
                ));

        assertTrue(counts.containsKey("ASN"), "Employment distribution should contain ASN");
        assertTrue(counts.containsKey("SWASTA"), "Employment distribution should contain SWASTA");
        assertEquals(1, counts.get("ASN"));
        assertEquals(1, counts.get("SWASTA"));
    }

    @Test
    void institutionTypeDistributionShouldShowCountsPerType() {
        List<Map<String, Object>> distribution = givenAuth(adminToken)
                .when()
                .get("/api/dashboard/institution-type-distribution")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .extract()
                .path("data");

        Map<String, Integer> counts = distribution.stream()
                .collect(Collectors.toMap(
                        entry -> entry.get("label").toString(),
                        entry -> ((Number) entry.get("count")).intValue(),
                        Integer::sum
                ));

        assertTrue(counts.containsKey("BPS_KOTA_KABUPATEN"), "Distribution should contain BPS_KOTA_KABUPATEN");
        assertTrue(counts.containsKey("SWASTA"), "Distribution should contain SWASTA");
        assertEquals(1, counts.get("BPS_KOTA_KABUPATEN"));
        assertEquals(1, counts.get("SWASTA"));
    }

    @Test
    void topInstitutionsShouldReturnRankedInstitutions() {
        List<String> institutionNames = givenAuth(adminToken)
                .when()
                .get("/api/dashboard/top-institutions?limit=5")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .extract()
                .path("data.institutionName");

        assertTrue(institutionNames.size() > 0, "Top institutions should not be empty");
        assertTrue(institutionNames.contains(primaryInstitution.getName()));
        assertTrue(institutionNames.contains(secondaryInstitution.getName()));
    }

    @Test
    void topLocationsShouldReturnProvinceAndCityCounts() {
        List<Map<String, Object>> locations = givenAuth(adminToken)
                .when()
                .get("/api/dashboard/top-locations?limit=5")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .extract()
                .path("data");

        Map<String, Integer> countsByCity = locations.stream()
                .collect(Collectors.toMap(
                        entry -> entry.get("city").toString(),
                        entry -> ((Number) entry.get("totalAlumni")).intValue(),
                        Integer::sum
                ));

        assertTrue(countsByCity.containsKey("Jakarta"));
        assertTrue(countsByCity.containsKey("Bandung"));
        assertEquals(1, countsByCity.get("Jakarta"));
        assertEquals(1, countsByCity.get("Bandung"));
    }

    @Test
    void angkatanDistributionShouldReturnCounts() {
        List<Map<String, Object>> distribution = givenAuth(adminToken)
                .when()
                .get("/api/dashboard/angkatan-distribution")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .extract()
                .path("data");

        Map<String, Integer> counts = distribution.stream()
                .collect(Collectors.toMap(
                        entry -> entry.get("label").toString(),
                        entry -> ((Number) entry.get("count")).intValue(),
                        Integer::sum
                ));
        long expectedAngkatan62 = userRepository.findAll().stream()
                .filter(user -> user.getAngkatan() == 62)
                .count();

        assertTrue(counts.containsKey("62"));
        assertEquals(Math.toIntExact(expectedAngkatan62), counts.get("62"));
    }

    @Test
    void workingStatusDistributionShouldIncludeWorkingAndNonWorking() {
        List<Map<String, Object>> distribution = givenAuth(adminToken)
                .when()
                .get("/api/dashboard/working-status")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .extract()
                .path("data");

        Map<String, Integer> counts = distribution.stream()
                .collect(Collectors.toMap(
                        entry -> entry.get("label").toString(),
                        entry -> ((Number) entry.get("count")).intValue(),
                        Integer::sum
                ));
        long expectedWorking = workHistoryRepository.countDistinctUserWithCurrentJob();
        long expectedNotWorking = userRepository.count() - expectedWorking;

        assertTrue(counts.containsKey("WORKING"));
        assertTrue(counts.containsKey("NOT_WORKING"));
        assertEquals(Math.toIntExact(expectedWorking), counts.get("WORKING"));
        assertEquals(Math.toIntExact(expectedNotWorking), counts.get("NOT_WORKING"));
    }
}

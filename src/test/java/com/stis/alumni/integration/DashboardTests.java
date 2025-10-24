package com.stis.alumni.integration;

import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.notNullValue;

class DashboardTests extends BaseIntegrationTest {

    @Test
    void summaryShouldReturnAggregateCounts() {
        long totalAlumni = userRepository.count();
        long totalInstitutions = institutionRepository.count();

        givenAuth(adminToken)
                .when()
                .get("/api/dashboard/summary")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.totalAlumni", equalTo((int) totalAlumni))
                .body("data.totalInstitutions", equalTo((int) totalInstitutions));
    }

    @Test
    void employmentDistributionShouldListCurrentEmploymentTypes() {
        givenAuth(adminToken)
                .when()
                .get("/api/dashboard/employment-distribution")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.find { it.label == 'ASN' }.count", equalTo(1))
                .body("data.find { it.label == 'SWASTA' }.count", equalTo(1));
    }

    @Test
    void institutionTypeDistributionShouldShowCountsPerType() {
        givenAuth(adminToken)
                .when()
                .get("/api/dashboard/institution-type-distribution")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.find { it.label == 'BPS_KOTA_KABUPATEN' }.count", equalTo(1))
                .body("data.find { it.label == 'SWASTA' }.count", equalTo(1));
    }

    @Test
    void topInstitutionsShouldReturnRankedInstitutions() {
        givenAuth(adminToken)
                .when()
                .get("/api/dashboard/top-institutions?limit=5")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.size()", greaterThan(0))
                .body("data.institutionName", hasItems(primaryInstitution.getName(), secondaryInstitution.getName()));
    }

    @Test
    void topLocationsShouldReturnProvinceAndCityCounts() {
        givenAuth(adminToken)
                .when()
                .get("/api/dashboard/top-locations?limit=5")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.size()", greaterThan(0))
                .body("data.find { it.city == 'Jakarta' }.totalAlumni", equalTo(1))
                .body("data.find { it.city == 'Bandung' }.totalAlumni", equalTo(1));
    }

    @Test
    void angkatanDistributionShouldReturnCounts() {
        givenAuth(adminToken)
                .when()
                .get("/api/dashboard/angkatan-distribution")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.find { it.label == '62' }.count", equalTo(3));
    }

    @Test
    void workingStatusDistributionShouldIncludeWorkingAndNonWorking() {
        givenAuth(adminToken)
                .when()
                .get("/api/dashboard/working-status")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.find { it.label == 'WORKING' }.count", equalTo(2))
                .body("data.find { it.label == 'NOT_WORKING' }.count", equalTo(1));
    }
}

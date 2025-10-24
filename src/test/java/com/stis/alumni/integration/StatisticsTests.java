package com.stis.alumni.integration;

import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.notNullValue;

class StatisticsTests extends BaseIntegrationTest {

    @Test
    void overviewShouldProvideAggregatedStatistics() {
        givenAuth(adminToken)
                .when()
                .get("/api/statistics/overview")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.totalAlumni", equalTo((int) userRepository.count()))
                .body("data.totalInstitutions", equalTo((int) institutionRepository.count()))
                .body("data.byInstitutionType.BPS_KOTA_KABUPATEN", equalTo(1))
                .body("data.byInstitutionType.SWASTA", equalTo(1));
    }

    @Test
    void alumniDistributionByProvinceShouldReturnValues() {
        givenAuth(adminToken)
                .when()
                .get("/api/statistics/alumni-distribution?groupBy=province")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.size()", greaterThan(0))
                .body("data.label", hasItems("DKI Jakarta", "Jawa Barat"));
    }

    @Test
    void alumniDistributionByInstitutionShouldReturnValues() {
        givenAuth(adminToken)
                .when()
                .get("/api/statistics/alumni-distribution?groupBy=institution")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.label", hasItems(primaryInstitution.getName(), secondaryInstitution.getName()));
    }

    @Test
    void alumniDistributionByTypeShouldReturnValues() {
        givenAuth(adminToken)
                .when()
                .get("/api/statistics/alumni-distribution?groupBy=type")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.label", hasItems("BPS_KOTA_KABUPATEN", "SWASTA"));
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

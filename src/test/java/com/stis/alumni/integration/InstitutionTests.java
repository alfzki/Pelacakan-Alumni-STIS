package com.stis.alumni.integration;

import com.stis.alumni.entity.Institution;
import com.stis.alumni.enums.InstitutionType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InstitutionTests extends BaseIntegrationTest {

    @Test
    void createInstitutionShouldReturnCreatedEntity() {
        Map<String, Object> request = new HashMap<>();
        request.put("name", "Integration Institution " + uniqueUsername("inst"));
        request.put("type", InstitutionType.SWASTA.name());
        request.put("province", "Jawa Tengah");
        request.put("city", "Semarang");
        request.put("address", "Jl. Contoh 123");
        request.put("description", "Created from integration test");

        givenAuth(adminToken)
                .body(request)
                .when()
                .post("/api/institutions")
                .then()
                .statusCode(201)
                .body("status", equalTo("success"))
                .body("data.id", notNullValue())
                .body("data.name", equalTo(request.get("name")));
    }

    @Test
    void listInstitutionsShouldReturnPagedContent() {
        givenAuth(userToken)
                .when()
                .get("/api/institutions")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.content.size()", greaterThan(0))
                .body("data.content.find { it.name == 'BPS Kota Jakarta' }", notNullValue());
    }

    @Test
    void getInstitutionDetailShouldReturnResponse() {
        givenAuth(userToken)
                .when()
                .get("/api/institutions/" + primaryInstitution.getId())
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.id", equalTo(primaryInstitution.getId().intValue()))
                .body("data.name", equalTo(primaryInstitution.getName()))
                .body("data.alumniCount", greaterThan(0));
    }

    @Test
    void updateInstitutionShouldPersistChanges() {
        Institution institution = createInstitution(
                "Temporary Institution",
                InstitutionType.BPS_PROVINSI,
                "Banten",
                "Serang",
                "Jl. Serang No. 5"
        );

        givenAuth(adminToken)
                .body(Map.of(
                        "name", "Updated Institution Name",
                        "province", "Bali",
                        "city", "Denpasar"
                ))
                .when()
                .put("/api/institutions/" + institution.getId())
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.name", equalTo("Updated Institution Name"))
                .body("data.province", equalTo("Bali"));

        Institution refreshed = institutionRepository.findById(institution.getId()).orElseThrow();
        assertEquals("Updated Institution Name", refreshed.getName());
        assertEquals("Bali", refreshed.getProvince());
    }

    @Test
    void deleteInstitutionShouldRemoveEntity() {
        Institution institution = createInstitution(
                "Deletable Institution",
                InstitutionType.SWASTA,
                "DI Yogyakarta",
                "Yogyakarta",
                "Jl. Malioboro"
        );

        givenAuth(adminToken)
                .when()
                .delete("/api/institutions/" + institution.getId())
                .then()
                .statusCode(200)
                .body("status", equalTo("success"));

        assertTrue(institutionRepository.findById(institution.getId()).isEmpty());
    }

    @Test
    void deleteInstitutionWithWorkHistoryShouldReturnBadRequest() {
        givenAuth(adminToken)
                .when()
                .delete("/api/institutions/" + primaryInstitution.getId())
                .then()
                .statusCode(400)
                .body("status", equalTo("error"));
    }

    @Test
    void optionsShouldReturnSelectableList() {
        givenAuth(userToken)
                .when()
                .get("/api/institutions/options?search=BPS")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.size()", greaterThan(0))
                .body("data.find { it.label == 'BPS Kota Jakarta' }", notNullValue())
                .body("data.find { it.label == 'Unknown' }", nullValue());
    }
}

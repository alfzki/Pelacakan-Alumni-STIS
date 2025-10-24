package com.stis.alumni.integration;

import com.stis.alumni.entity.WorkHistory;
import com.stis.alumni.enums.EmploymentType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorkHistoryTests extends BaseIntegrationTest {

    @Test
    void createWorkHistoryShouldPersistNewEntry() {
        Map<String, Object> request = new HashMap<>();
        request.put("institutionId", secondaryInstitution.getId());
        request.put("position", "Project Lead " + uniqueUsername("role"));
        request.put("employmentType", EmploymentType.PPPK.name());
        request.put("startDate", "2022-01-01");
        request.put("isCurrentJob", false);
        request.put("description", "Leading important projects");

        givenAuth(userToken)
                .body(request)
                .when()
                .post("/api/work-histories")
                .then()
                .statusCode(201)
                .body("status", equalTo("success"))
                .body("data.id", notNullValue())
                .body("data.position", equalTo(request.get("position")));
    }

    @Test
    void listWorkHistoriesShouldReturnEntriesForCurrentUser() {
        givenAuth(userToken)
                .when()
                .get("/api/work-histories")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.size()", greaterThanOrEqualTo(1))
                .body("data.find { it.id == " + regularCurrentWorkHistory.getId() + " }.institution.name",
                        equalTo(primaryInstitution.getName()));
    }

    @Test
    void getWorkHistoryDetailShouldReturnRequestedEntry() {
        givenAuth(userToken)
                .when()
                .get("/api/work-histories/" + regularCurrentWorkHistory.getId())
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.id", equalTo(regularCurrentWorkHistory.getId().intValue()))
                .body("data.position", equalTo(regularCurrentWorkHistory.getPosition()));
    }

    @Test
    void updateWorkHistoryShouldApplyChanges() {
        WorkHistory toUpdate = createWorkHistory(
                regularUser,
                secondaryInstitution,
                EmploymentType.SWASTA,
                "Temporary Role",
                LocalDate.of(2019, 1, 1),
                LocalDate.of(2020, 12, 31),
                false,
                "Temporary assignment"
        );

        givenAuth(userToken)
                .body(Map.of(
                        "position", "Updated Role",
                        "employmentType", EmploymentType.ASN.name(),
                        "description", "Updated description",
                        "isCurrentJob", false
                ))
                .when()
                .put("/api/work-histories/" + toUpdate.getId())
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.position", equalTo("Updated Role"));

        WorkHistory refreshed = workHistoryRepository.findById(toUpdate.getId()).orElseThrow();
        assertEquals("Updated Role", refreshed.getPosition());
        assertEquals(EmploymentType.ASN, refreshed.getEmploymentType());
    }

    @Test
    void deleteWorkHistoryShouldRemoveEntry() {
        WorkHistory toDelete = createWorkHistory(
                regularUser,
                secondaryInstitution,
                EmploymentType.HONORER,
                "Seasonal Role",
                LocalDate.of(2018, 1, 1),
                LocalDate.of(2018, 12, 31),
                false,
                "Seasonal assignment"
        );

        givenAuth(userToken)
                .when()
                .delete("/api/work-histories/" + toDelete.getId())
                .then()
                .statusCode(200)
                .body("status", equalTo("success"));

        assertTrue(workHistoryRepository.findById(toDelete.getId()).isEmpty());
    }

    @Test
    void getWorkHistoryOwnedByAnotherUserShouldReturnForbidden() {
        givenAuth(secondaryUserToken)
                .when()
                .get("/api/work-histories/" + regularCurrentWorkHistory.getId())
                .then()
                .statusCode(403)
                .body("status", equalTo("error"));
    }
}

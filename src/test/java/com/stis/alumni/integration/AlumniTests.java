package com.stis.alumni.integration;

import com.stis.alumni.entity.User;
import com.stis.alumni.enums.UserRole;
import com.stis.alumni.enums.UserStatus;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

class AlumniTests extends BaseIntegrationTest {

    @Test
    void listAlumniShouldReturnPagedDataForAdmin() {
        givenAuth(adminToken)
                .when()
                .get("/api/alumni")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.content.size()", greaterThan(0))
                .body("data.content.find { it.id == " + regularUser.getId() + " }.username",
                        equalTo(regularUser.getUsername()));
    }

    @Test
    void listAlumniShouldRejectNonAdminUsers() {
        givenAuth(userToken)
                .when()
                .get("/api/alumni")
                .then()
                .statusCode(403);
    }

    @Test
    void listAlumniShouldRequireAuthentication() {
        givenJson()
                .when()
                .get("/api/alumni")
                .then()
                .statusCode(401);
    }

    @Test
    void listAlumniShouldSupportFilteringAndSearch() {
        User inactiveUser = createUser(
                uniqueUsername("inactive"),
                uniqueEmail("inactive"),
                "Inactive123!",
                UserRole.USER,
                uniqueNim()
        );
        inactiveUser.setStatus(UserStatus.INACTIVE);
        inactiveUser.setAngkatan(50);
        inactiveUser.setProgramStudi("D3");
        inactiveUser.setTahunLulus(2020);
        inactiveUser.setFullName("Filtered Alumni");
        userRepository.save(inactiveUser);

        givenAuth(adminToken)
                .queryParam("angkatan", 50)
                .queryParam("programStudi", "D3")
                .queryParam("tahunLulus", 2020)
                .queryParam("status", "INACTIVE")
                .queryParam("search", "Filtered")
                .when()
                .get("/api/alumni")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.content", hasSize(1))
                .body("data.content[0].id", equalTo(inactiveUser.getId().intValue()))
                .body("data.content[0].status", equalTo("INACTIVE"))
                .body("data.content[0].role", equalTo("USER"));
    }

    @Test
    void listAlumniWithInvalidStatusShouldReturnBadRequest() {
        givenAuth(adminToken)
                .queryParam("status", "UNKNOWN")
                .when()
                .get("/api/alumni")
                .then()
                .statusCode(400)
                .body("status", equalTo("error"))
                .body("message", equalTo("Invalid status value: UNKNOWN"))
                .body("errors", nullValue());
    }

    @Test
    void getAlumniDetailShouldReturnProfileForAdmin() {
        givenAuth(adminToken)
                .when()
                .get("/api/alumni/" + regularUser.getId())
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.id", equalTo(regularUser.getId().intValue()))
                .body("data.username", equalTo(regularUser.getUsername()))
                .body("data.workHistories", notNullValue());
    }

    @Test
    void getAlumniDetailShouldRejectNonAdminUsers() {
        givenAuth(userToken)
                .when()
                .get("/api/alumni/" + regularUser.getId())
                .then()
                .statusCode(403);
    }

    @Test
    void getAlumniDetailForUnknownIdShouldReturnNotFound() {
        givenAuth(adminToken)
                .when()
                .get("/api/alumni/99999")
                .then()
                .statusCode(404)
                .body("status", equalTo("error"))
                .body("message", equalTo("Alumni not found with id: 99999"));
    }
}

package com.stis.alumni.integration;

import com.stis.alumni.entity.User;
import com.stis.alumni.enums.UserRole;
import com.stis.alumni.enums.UserStatus;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserTests extends BaseIntegrationTest {

    @Test
    void getProfileShouldReturnCurrentUser() {
        givenAuth(userToken)
                .when()
                .get("/api/users/profile")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.username", equalTo(regularUser.getUsername()))
                .body("data.email", equalTo(regularUser.getEmail()))
                .body("data.jurusan", equalTo(regularUser.getJurusan()));
    }

    @Test
    void updateProfileShouldModifyUserData() {
        Map<String, Object> request = new HashMap<>();
        request.put("fullName", "Updated User Name");
        request.put("programStudi", "D4");
        request.put("jurusan", "D4 statistik");
        request.put("tahunLulus", 2025);
        request.put("phoneNumber", "081200000001");
        request.put("alamat", "Alamat Baru");

        givenAuth(userToken)
                .body(request)
                .when()
                .put("/api/users/profile")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.fullName", equalTo("Updated User Name"))
                .body("data.phoneNumber", equalTo("081200000001"))
                .body("data.jurusan", equalTo("D4 statistik"));

        User updated = userRepository.findById(regularUser.getId()).orElseThrow();
        assertEquals("Updated User Name", updated.getFullName());
        assertEquals("081200000001", updated.getPhoneNumber());
        assertEquals("D4 statistik", updated.getJurusan());
    }

    @Test
    void changePasswordShouldUpdateStoredPassword() {
        User tempUser = createUser(
                uniqueUsername("changer"),
                uniqueEmail("changer"),
                "ChangeMe123!",
                UserRole.USER,
                uniqueNim()
        );
        String token = login(tempUser.getUsername(), "ChangeMe123!");

        givenAuth(token)
                .body(Map.of(
                        "oldPassword", "ChangeMe123!",
                        "newPassword", "NewPassword123!",
                        "confirmNewPassword", "NewPassword123!"
                ))
                .when()
                .put("/api/users/change-password")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"));

        String newToken = login(tempUser.getUsername(), "NewPassword123!");
        assertNotNull(newToken);
    }

    @Test
    void deleteAccountShouldMarkUserInactive() {
        User deletableUser = createUser(
                uniqueUsername("delete"),
                uniqueEmail("delete"),
                "DeleteMe123!",
                UserRole.USER,
                uniqueNim()
        );
        String token = login(deletableUser.getUsername(), "DeleteMe123!");

        givenAuth(token)
                .body(Map.of(
                        "password", "DeleteMe123!",
                        "confirmation", "DELETE"
                ))
                .when()
                .delete("/api/users/account")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("message", notNullValue());

        User refreshed = userRepository.findById(deletableUser.getId()).orElseThrow();
        assertEquals(UserStatus.INACTIVE, refreshed.getStatus());
    }
}

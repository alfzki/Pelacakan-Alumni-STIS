package com.stis.alumni.integration;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;

class AuthTests extends BaseIntegrationTest {

    @Test
    void registerShouldCreateUser() {
        Map<String, Object> payload = new HashMap<>();
        String username = uniqueUsername("newuser");
        payload.put("username", username);
        payload.put("email", uniqueEmail("newuser"));
        payload.put("password", "Password123!");
        payload.put("confirmPassword", "Password123!");
        payload.put("fullName", "New User");
        payload.put("nim", uniqueNim());
        payload.put("angkatan", 62);
        payload.put("programStudi", "D4");
        payload.put("jurusan", "D4 komputasi statistik");
        payload.put("tahunLulus", 2024);
        payload.put("phoneNumber", "081234567891");
        payload.put("alamat", "Jl. Baru 123");

        givenJson()
                .body(payload)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(201)
                .body("status", equalTo("success"))
                .body("data.username", equalTo(username))
                .body("data.jurusan", equalTo("D4 komputasi statistik"));
    }

    @Test
    void loginShouldReturnToken() {
        givenJson()
                .body(Map.of("username", regularUser.getUsername(), "password", USER_PASSWORD))
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("data.accessToken", not(isEmptyString()));
    }

    @Test
    void loginShouldFailWithInvalidPassword() {
        givenJson()
                .body(Map.of("username", regularUser.getUsername(), "password", "WrongPassword!1"))
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(401)
                .body("status", equalTo("error"))
                .body("message", equalTo("Invalid username or password"));
    }
}

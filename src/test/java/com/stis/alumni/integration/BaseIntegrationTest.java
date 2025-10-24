package com.stis.alumni.integration;

import com.stis.alumni.entity.Institution;
import com.stis.alumni.entity.User;
import com.stis.alumni.entity.WorkHistory;
import com.stis.alumni.enums.EmploymentType;
import com.stis.alumni.enums.InstitutionType;
import com.stis.alumni.enums.UserRole;
import com.stis.alumni.enums.UserStatus;
import com.stis.alumni.repository.InstitutionRepository;
import com.stis.alumni.repository.UserRepository;
import com.stis.alumni.repository.WorkHistoryRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseIntegrationTest {

    protected static final String ADMIN_PASSWORD = "AdminPass123!";
    protected static final String USER_PASSWORD = "UserPass123!";

    @LocalServerPort
    protected int port;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected InstitutionRepository institutionRepository;

    @Autowired
    protected WorkHistoryRepository workHistoryRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected User adminUser;
    protected User regularUser;
    protected User secondaryUser;
    protected Institution primaryInstitution;
    protected Institution secondaryInstitution;
    protected WorkHistory regularCurrentWorkHistory;
    protected WorkHistory secondaryCurrentWorkHistory;
    protected String adminToken;
    protected String userToken;
    protected String secondaryUserToken;

    @BeforeAll
    void configureRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    void resetAndSeedBaseData() {
        // Ensure each test runs with a clean database snapshot
        resetData();
        seedReferenceData();
    }

    protected void resetData() {
        workHistoryRepository.deleteAll();
        institutionRepository.deleteAll();
        userRepository.deleteAll();
    }

    private void seedReferenceData() {
        adminUser = createUser("admin", "admin@example.com", ADMIN_PASSWORD, UserRole.ADMIN, "1000000000001");
        regularUser = createUser("user.one", "user1@example.com", USER_PASSWORD, UserRole.USER, "1000000000002");
        secondaryUser = createUser("user.two", "user2@example.com", USER_PASSWORD, UserRole.USER, "1000000000003");

        primaryInstitution = createInstitution(
                "BPS Kota Jakarta",
                InstitutionType.BPS_KOTA_KABUPATEN,
                "DKI Jakarta",
                "Jakarta",
                "Jl. Jakarta No. 1"
        );
        secondaryInstitution = createInstitution(
                "Perusahaan Swasta Bandung",
                InstitutionType.SWASTA,
                "Jawa Barat",
                "Bandung",
                "Jl. Bandung No. 2"
        );

        regularCurrentWorkHistory = createWorkHistory(
                regularUser,
                primaryInstitution,
                EmploymentType.ASN,
                "Statistician",
                LocalDate.of(2020, 1, 1),
                null,
                true,
                "Analyzes statistical data"
        );
        secondaryCurrentWorkHistory = createWorkHistory(
                secondaryUser,
                secondaryInstitution,
                EmploymentType.SWASTA,
                "Data Analyst",
                LocalDate.of(2021, 5, 1),
                null,
                true,
                "Works on private sector analytics"
        );

        adminToken = login(adminUser.getUsername(), ADMIN_PASSWORD);
        userToken = login(regularUser.getUsername(), USER_PASSWORD);
        secondaryUserToken = login(secondaryUser.getUsername(), USER_PASSWORD);
    }

    protected User createUser(String username, String email, String rawPassword, UserRole role, String nim) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setFullName("Test " + username);
        user.setNim(nim);
        user.setAngkatan(62);
        user.setProgramStudi("D4");
        user.setTahunLulus(2024);
        user.setPhoneNumber("081234567890");
        user.setAlamat("Test Address");
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);
        return userRepository.save(user);
    }

    protected Institution createInstitution(String name, InstitutionType type, String province, String city, String address) {
        Institution institution = new Institution();
        institution.setName(name);
        institution.setType(type);
        institution.setProvince(province);
        institution.setCity(city);
        institution.setAddress(address);
        institution.setDescription("Integration test data for " + name);
        return institutionRepository.save(institution);
    }

    protected WorkHistory createWorkHistory(User user,
                                            Institution institution,
                                            EmploymentType employmentType,
                                            String position,
                                            LocalDate startDate,
                                            LocalDate endDate,
                                            boolean currentJob,
                                            String description) {
        WorkHistory workHistory = new WorkHistory();
        workHistory.setUser(user);
        workHistory.setInstitution(institution);
        workHistory.setEmploymentType(employmentType);
        workHistory.setPosition(position);
        workHistory.setStartDate(startDate);
        workHistory.setEndDate(endDate);
        workHistory.setCurrentJob(currentJob);
        workHistory.setDescription(description);
        return workHistoryRepository.save(workHistory);
    }

    protected RequestSpecification givenAuth(String token) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token);
    }

    protected RequestSpecification givenJson() {
        return RestAssured.given()
                .contentType(ContentType.JSON);
    }

    protected String login(String username, String password) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("username", username, "password", password))
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("data.accessToken");
    }

    protected String uniqueUsername(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    protected String uniqueEmail(String prefix) {
        return prefix + "+" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
    }

    protected String uniqueNim() {
        long value = Math.abs(UUID.randomUUID().getMostSignificantBits());
        return String.format("%020d", value).substring(0, 12);
    }
}

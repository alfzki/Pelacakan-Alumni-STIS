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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.MariaDBContainer;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(scripts = "classpath:sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/seed_base_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public abstract class BaseIntegrationTest {

    protected static final String ADMIN_PASSWORD = "AdminPass123!";
    protected static final String USER_PASSWORD = "UserPass123!";

    private static final MariaDBContainer<?> database = new MariaDBContainer<>("mariadb:11.4")
            .withDatabaseName("tracking_alumni_test")
            .withUsername("test_user")
            .withPassword("test_pass");

    static {
        database.start();
    }

    @DynamicPropertySource
    static void overrideDataSourceProperties(DynamicPropertyRegistry registry) {
        if (!database.isRunning()) {
            database.start();
        }
        registry.add("spring.datasource.url", database::getJdbcUrl);
        registry.add("spring.datasource.username", database::getUsername);
        registry.add("spring.datasource.password", database::getPassword);
        registry.add("spring.datasource.driver-class-name", database::getDriverClassName);
    }

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
    void loadSeedData() {
        adminUser = userRepository.findByUsername("admin")
                .orElseThrow(() -> new IllegalStateException("Seed user 'admin' missing"));
        regularUser = userRepository.findByUsername("user.one")
                .orElseThrow(() -> new IllegalStateException("Seed user 'user.one' missing"));
        secondaryUser = userRepository.findByUsername("user.two")
                .orElseThrow(() -> new IllegalStateException("Seed user 'user.two' missing"));

        primaryInstitution = institutionRepository.findByNameIgnoreCase("BPS Kota Jakarta")
                .orElseThrow(() -> new IllegalStateException("Seed institution 'BPS Kota Jakarta' missing"));
        secondaryInstitution = institutionRepository.findByNameIgnoreCase("Perusahaan Swasta Bandung")
                .orElseThrow(() -> new IllegalStateException("Seed institution 'Perusahaan Swasta Bandung' missing"));

        regularCurrentWorkHistory = workHistoryRepository.findByUserIdOrderByStartDateDesc(regularUser.getId())
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Seed work history for regular user missing"));
        secondaryCurrentWorkHistory = workHistoryRepository.findByUserIdOrderByStartDateDesc(secondaryUser.getId())
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Seed work history for secondary user missing"));

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
        user.setJurusan("D4 statistik");
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

package com.example.english_center.test;

import com.example.english_center.entity.Users;
import com.example.english_center.enums.Gender;
import com.example.english_center.enums.Role;
import com.example.english_center.enums.UserStatus;
import com.example.english_center.repository.StudentProfilesRepository;
import com.example.english_center.repository.TeacherProfilesRepository;
import com.example.english_center.repository.UsersRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QuickApiFlowTests {

    private static final String PASSWORD = "Test123456";

    @LocalServerPort
    private int port;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private TeacherProfilesRepository teacherProfilesRepository;

    @Autowired
    private StudentProfilesRepository studentProfilesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Set<String> emailsToCleanUp = new HashSet<>();

    @AfterEach
    void cleanUpTestUsers() {
        for (String email : emailsToCleanUp) {
            usersRepository.findByEmail(email).ifPresent(usersRepository::delete);
        }
        emailsToCleanUp.clear();
    }

    @Test
    void loginEndpointReturnsJwt() throws Exception {
        String email = uniqueEmail("api-login");
        createUser(email, PASSWORD, Role.MANAGER);

        HttpResponse<String> response = postJson("/auth/login", loginJson(email, PASSWORD), null);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body().split("\\.")).hasSize(3);
    }

    @Test
    void studentTokenCannotCallManagerEndpoint() throws Exception {
        String email = uniqueEmail("api-student-authz");
        createUser(email, PASSWORD, Role.STUDENT);
        String token = loginAndGetToken(email);

        HttpResponse<String> response = get("/manager/users", token);

        assertThat(response.statusCode()).isEqualTo(403);
    }

    @Test
    void managerCanCreateTeacherThroughEndpoint() throws Exception {
        String managerEmail = uniqueEmail("api-manager-create");
        createUser(managerEmail, PASSWORD, Role.MANAGER);
        String token = loginAndGetToken(managerEmail);
        String teacherEmail = uniqueEmail("api-created-teacher");

        HttpResponse<String> response = postJson(
                "/manager/users/teachers",
                userAccountJson("API Teacher", teacherEmail, "0920000001", "MALE"),
                token
        );

        assertThat(response.statusCode()).isEqualTo(200);
        Users teacher = usersRepository.findByEmail(teacherEmail).orElseThrow();
        assertThat(teacher.getRole()).isEqualTo(Role.TEACHER);
        assertThat(teacherProfilesRepository.findById(teacher.getId()))
                .isPresent()
                .get()
                .satisfies(profile -> assertThat(profile.getTeacherCode()).isEqualTo("T" + teacher.getId()));
    }

    @Test
    void managerCanCreateStudentThroughEndpoint() throws Exception {
        String managerEmail = uniqueEmail("api-manager-create-student");
        createUser(managerEmail, PASSWORD, Role.MANAGER);
        String token = loginAndGetToken(managerEmail);
        String studentEmail = uniqueEmail("api-created-student");

        HttpResponse<String> response = postJson(
                "/manager/users/students",
                userAccountJson("API Student", studentEmail, "0920000002", "FEMALE"),
                token
        );

        assertThat(response.statusCode()).isEqualTo(200);
        Users student = usersRepository.findByEmail(studentEmail).orElseThrow();
        assertThat(student.getRole()).isEqualTo(Role.STUDENT);
        assertThat(studentProfilesRepository.findById(student.getId()))
                .isPresent()
                .get()
                .satisfies(profile -> assertThat(profile.getStudentCode()).isEqualTo("S" + student.getId()));
    }

    @Test
    void managerCanUpdateUserThroughEndpoint() throws Exception {
        String managerEmail = uniqueEmail("api-manager-update");
        createUser(managerEmail, PASSWORD, Role.MANAGER);
        String token = loginAndGetToken(managerEmail);
        Users target = createUser(uniqueEmail("api-target-update"), PASSWORD, Role.STUDENT);
        String updatedEmail = uniqueEmail("api-target-updated");

        HttpResponse<String> response = putJson(
                "/manager/users/" + target.getId(),
                """
                        {
                          "fullname": "API Updated Student",
                          "email": "%s",
                          "phoneNumber": "0930000001",
                          "gender": "OTHER",
                          "avatarUrl": "https://example.com/api-avatar.png"
                        }
                        """.formatted(updatedEmail),
                token
        );

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("\"email\":\"" + updatedEmail + "\"");
        assertThat(usersRepository.findByEmail(updatedEmail))
                .isPresent()
                .get()
                .satisfies(user -> {
                    assertThat(user.getFullname()).isEqualTo("API Updated Student");
                    assertThat(user.getGender()).isEqualTo(Gender.OTHER);
                });
    }

    @Test
    void managerCanFilterUsersByEnumThroughEndpoint() throws Exception {
        String managerEmail = uniqueEmail("api-manager-filter");
        createUser(managerEmail, PASSWORD, Role.MANAGER);
        String token = loginAndGetToken(managerEmail);
        String teacherEmail = uniqueEmail("api-teacher-filter");
        createUser(teacherEmail, PASSWORD, Role.TEACHER, Gender.FEMALE, UserStatus.ACTIVE);
        createUser(uniqueEmail("api-student-filter"), PASSWORD, Role.STUDENT, Gender.MALE, UserStatus.ACTIVE);

        HttpResponse<String> response = get(
                "/manager/users?keyword=%s&role=TEACHER&status=ACTIVE&gender=FEMALE".formatted(urlEncode(teacherEmail)),
                token
        );

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("\"email\":\"" + teacherEmail + "\"");
        assertThat(response.body()).contains("\"role\":\"TEACHER\"");
        assertThat(response.body()).contains("\"status\":\"ACTIVE\"");
        assertThat(response.body()).contains("\"gender\":\"FEMALE\"");
    }

    private String loginAndGetToken(String email) throws IOException, InterruptedException {
        HttpResponse<String> response = postJson("/auth/login", loginJson(email, PASSWORD), null);
        assertThat(response.statusCode()).isEqualTo(200);
        return response.body();
    }

    private HttpResponse<String> get(String path, String token) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri(path)).GET();
        addBearerToken(builder, token);
        return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> postJson(String path, String json, String token) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri(path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json));
        addBearerToken(builder, token);
        return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> putJson(String path, String json, String token) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri(path))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json));
        addBearerToken(builder, token);
        return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private void addBearerToken(HttpRequest.Builder builder, String token) {
        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }
    }

    private URI uri(String path) {
        return URI.create("http://localhost:" + port + path);
    }

    private String loginJson(String email, String password) {
        return """
                {
                  "email": "%s",
                  "password": "%s"
                }
                """.formatted(email, password);
    }

    private String userAccountJson(String fullname, String email, String phoneNumber, String gender) {
        return """
                {
                  "fullname": "%s",
                  "email": "%s",
                  "password": "%s",
                  "phoneNumber": "%s",
                  "gender": "%s"
                }
                """.formatted(fullname, email, PASSWORD, phoneNumber, gender);
    }

    private Users createUser(String email, String password, Role role) {
        return createUser(email, password, role, Gender.OTHER, UserStatus.ACTIVE);
    }

    private Users createUser(String email, String password, Role role, Gender gender, UserStatus status) {
        emailsToCleanUp.add(email);

        Users user = new Users();
        user.setFullname("Quick API Test User");
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setPhoneNumber("0900000000");
        user.setGender(gender);
        user.setRole(role);
        user.setStatus(status);

        return usersRepository.save(user);
    }

    private String uniqueEmail(String label) {
        String safeLabel = label.replaceAll("[^A-Za-z0-9]", "");
        String email = "quick%s%s@example.com".formatted(safeLabel, UUID.randomUUID().toString().replace("-", ""));
        emailsToCleanUp.add(email);
        return email;
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}

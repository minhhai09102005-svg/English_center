package com.example.english_center.test;

import com.example.english_center.customExceptions.InvalidLoginException;
import com.example.english_center.dto.request.LoginRequest;
import com.example.english_center.dto.request.UserAccountRequest;
import com.example.english_center.dto.request.UserAccountUpdateRequest;
import com.example.english_center.dto.response.UserAccountResponse;
import com.example.english_center.entity.Users;
import com.example.english_center.enums.Gender;
import com.example.english_center.enums.Role;
import com.example.english_center.enums.UserStatus;
import com.example.english_center.repository.StudentProfilesRepository;
import com.example.english_center.repository.TeacherProfilesRepository;
import com.example.english_center.repository.UsersRepository;
import com.example.english_center.security.CustomJwtAuthConverter;
import com.example.english_center.service.AuthService;
import com.example.english_center.service.UserAccountService;
import com.example.english_center.utils.JwtTokenProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class QuickFlowTests {

    private static final String PASSWORD = "Test123456";

    @Autowired
    private AuthService authService;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private TeacherProfilesRepository teacherProfilesRepository;

    @Autowired
    private StudentProfilesRepository studentProfilesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private CustomJwtAuthConverter customJwtAuthConverter;

    private final Set<String> emailsToCleanUp = new HashSet<>();

    @AfterEach
    void cleanUpTestUsers() {
        SecurityContextHolder.clearContext();

        for (String email : emailsToCleanUp) {
            usersRepository.findByEmail(email).ifPresent(usersRepository::delete);
        }
        emailsToCleanUp.clear();
    }

    @Test
    void loginWithValidAccountReturnsJwt() {
        String email = uniqueEmail("manager-login");
        createUser(email, PASSWORD, Role.MANAGER);

        String token = authService.login(loginRequest(email, PASSWORD));

        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void loginWithWrongPasswordThrowsInvalidLoginException() {
        String email = uniqueEmail("manager-wrong-password");
        createUser(email, PASSWORD, Role.MANAGER);

        assertThatThrownBy(() -> authService.login(loginRequest(email, "wrong-password")))
                .isInstanceOf(InvalidLoginException.class);
    }

    @Test
    void jwtContainsUserIdEmailAndRole() {
        Users manager = createUser(uniqueEmail("manager-jwt"), PASSWORD, Role.MANAGER);

        Jwt jwt = jwtDecoder.decode(jwtTokenProvider.generateToken(manager));
        var authentication = customJwtAuthConverter.convert(jwt);

        assertThat(jwt.getSubject()).isEqualTo(manager.getId().toString());
        assertThat(jwt.getClaimAsString("email")).isEqualTo(manager.getEmail());
        assertThat(jwt.getClaimAsString("role")).isEqualTo(Role.MANAGER.name());
        assertThat(authentication.getAuthorities())
                .extracting(Object::toString)
                .containsExactly("ROLE_MANAGER");
    }

    @Test
    void studentCannotUseManagerUserAccountService() {
        Users student = createUser(uniqueEmail("student-authz"), PASSWORD, Role.STUDENT);
        authenticateAs(student);

        assertThatThrownBy(() -> userAccountService.searchUserAccounts(Map.of("keyword", student.getEmail())))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void managerCanCreateTeacherAndStudentAccounts() {
        Users manager = createUser(uniqueEmail("manager-create"), PASSWORD, Role.MANAGER);
        authenticateAs(manager);

        String teacherEmail = uniqueEmail("created-teacher");
        String studentEmail = uniqueEmail("created-student");

        userAccountService.createTeacherAccount(userAccountRequest("Teacher Created", teacherEmail, "0900000001", Gender.MALE));
        userAccountService.createStudentAccount(userAccountRequest("Student Created", studentEmail, "0900000002", Gender.FEMALE));

        Users teacher = usersRepository.findByEmail(teacherEmail).orElseThrow();
        Users student = usersRepository.findByEmail(studentEmail).orElseThrow();

        assertThat(teacher.getRole()).isEqualTo(Role.TEACHER);
        assertThat(student.getRole()).isEqualTo(Role.STUDENT);
        assertThat(teacherProfilesRepository.findById(teacher.getId()))
                .isPresent()
                .get()
                .satisfies(profile -> assertThat(profile.getTeacherCode()).isEqualTo("T" + teacher.getId()));
        assertThat(studentProfilesRepository.findById(student.getId()))
                .isPresent()
                .get()
                .satisfies(profile -> assertThat(profile.getStudentCode()).isEqualTo("S" + student.getId()));
    }

    @Test
    void managerCanUpdateDeactivateAndActivateUser() {
        Users manager = createUser(uniqueEmail("manager-update"), PASSWORD, Role.MANAGER);
        Users target = createUser(uniqueEmail("target-update"), PASSWORD, Role.STUDENT);
        authenticateAs(manager);

        String updatedEmail = uniqueEmail("target-updated");
        UserAccountResponse updated = userAccountService.updateUserAccount(
                target.getId(),
                userAccountUpdateRequest("Updated Student", updatedEmail, "0911111111", Gender.OTHER)
        );

        assertThat(updated.getEmail()).isEqualTo(updatedEmail);
        assertThat(updated.getFullname()).isEqualTo("Updated Student");

        userAccountService.deactivateUserAccount(target.getId());
        assertThat(usersRepository.findById(target.getId()))
                .isPresent()
                .get()
                .extracting(Users::getStatus)
                .isEqualTo(UserStatus.INACTIVE);

        userAccountService.activateUserAccount(target.getId());
        assertThat(usersRepository.findById(target.getId()))
                .isPresent()
                .get()
                .extracting(Users::getStatus)
                .isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void managerCanFilterUsersByKeywordRoleStatusAndGender() {
        Users manager = createUser(uniqueEmail("manager-filter"), PASSWORD, Role.MANAGER);
        authenticateAs(manager);

        String teacherEmail = uniqueEmail("teacher-filter");
        createUser(teacherEmail, PASSWORD, Role.TEACHER, Gender.FEMALE, UserStatus.ACTIVE);
        createUser(uniqueEmail("student-filter"), PASSWORD, Role.STUDENT, Gender.MALE, UserStatus.ACTIVE);

        List<UserAccountResponse> result = userAccountService.searchUserAccounts(Map.of(
                "keyword", teacherEmail,
                "role", "TEACHER",
                "status", "ACTIVE",
                "gender", "FEMALE"
        ));

        assertThat(result)
                .singleElement()
                .satisfies(user -> {
                    assertThat(user.getEmail()).isEqualTo(teacherEmail);
                    assertThat(user.getRole()).isEqualTo(Role.TEACHER);
                    assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
                    assertThat(user.getGender()).isEqualTo(Gender.FEMALE);
                });
    }

    private LoginRequest loginRequest(String email, String password) {
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);
        return request;
    }

    private UserAccountRequest userAccountRequest(String fullname, String email, String phoneNumber, Gender gender) {
        UserAccountRequest request = new UserAccountRequest();
        request.setFullname(fullname);
        request.setEmail(email);
        request.setPassword(PASSWORD);
        request.setPhoneNumber(phoneNumber);
        request.setGender(gender);
        return request;
    }

    private UserAccountUpdateRequest userAccountUpdateRequest(String fullname, String email, String phoneNumber, Gender gender) {
        UserAccountUpdateRequest request = new UserAccountUpdateRequest();
        request.setFullname(fullname);
        request.setEmail(email);
        request.setPhoneNumber(phoneNumber);
        request.setGender(gender);
        request.setAvatarUrl("https://example.com/avatar.png");
        return request;
    }

    private Users createUser(String email, String password, Role role) {
        return createUser(email, password, role, Gender.OTHER, UserStatus.ACTIVE);
    }

    private Users createUser(String email, String password, Role role, Gender gender, UserStatus status) {
        emailsToCleanUp.add(email);

        Users user = new Users();
        user.setFullname("Quick Test User");
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setPhoneNumber("0900000000");
        user.setGender(gender);
        user.setRole(role);
        user.setStatus(status);

        return usersRepository.save(user);
    }

    private void authenticateAs(Users user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                )
        );
    }

    private String uniqueEmail(String label) {
        String safeLabel = label.replaceAll("[^A-Za-z0-9]", "");
        String email = "quick%s%s@example.com".formatted(safeLabel, UUID.randomUUID().toString().replace("-", ""));
        emailsToCleanUp.add(email);
        return email;
    }
}

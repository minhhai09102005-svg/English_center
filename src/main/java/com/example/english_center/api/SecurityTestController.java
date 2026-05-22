package com.example.english_center.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SecurityTestController {
    private final PasswordEncoder passwordEncoder;
    @GetMapping("/")
    public String home() {
        return "English Center API is running";
    }

    @GetMapping("/security/public")
    public String publicPage() {
        return "PUBLIC: Ai cũng xem được, không cần đăng nhập.";
    }

    @GetMapping("/security/auth")
    public String authenticatedPage(Authentication authentication) {
        return "AUTH: Đã đăng nhập với username = " + authentication.getName();
    }

    @GetMapping("/security/manager")
    public String managerPage(Authentication authentication) {
        return "MANAGER: Xin chào quản lý " + authentication.getName();
    }

    @GetMapping("/security/teacher")
    public String teacherPage(Authentication authentication) {
        return "TEACHER: Xin chào giáo viên " + authentication.getName();
    }

    @GetMapping("/security/student")
    public String studentPage(Authentication authentication) {
        return "STUDENT: Xin chào học sinh " + authentication.getName();
    }

    @GetMapping("/security/staff")
    public String staffPage(Authentication authentication) {
        return "STAFF: Chỉ quản lý hoặc giáo viên vào được. Username = "
                + authentication.getName();
    }

    @GetMapping("/security/encode")
    public String encodePassword() {
        return passwordEncoder.encode("123456");
    }
}
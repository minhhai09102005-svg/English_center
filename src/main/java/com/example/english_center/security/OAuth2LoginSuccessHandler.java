package com.example.english_center.security;

import com.example.english_center.entity.Users;
import com.example.english_center.enums.Gender;
import com.example.english_center.enums.Role;
import com.example.english_center.enums.UserStatus;
import com.example.english_center.repository.UsersRepository;
import com.example.english_center.utils.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    // Tìm hoặc lưu user trong bảng users
    private final UsersRepository usersRepository;

    // Sinh JWT token của hệ thống English Center
    private final JwtTokenProvider jwtTokenProvider;

    // Hash password giả cho user đăng nhập bằng OAuth2
    private final PasswordEncoder passwordEncoder;

    /*
     * Hàm này chạy sau khi đăng nhập OAuth2 thành công.
     * Dùng được cho cả GitHub và Google.
     */
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        // Lấy thông tin user từ provider: GitHub hoặc Google
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        // Google thường có email, name, sub
        // GitHub thường có login, name, email có thể null
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String login = oauth2User.getAttribute("login");
        String sub = oauth2User.getAttribute("sub");

        /*
         * Nếu provider không trả email:
         * - GitHub: dùng login để tạo email giả
         * - Google: dùng sub để tạo email giả
         */
        if (email == null || email.isBlank()) {
            if (login != null && !login.isBlank()) {
                email = login + "@github.local";
            } else if (sub != null && !sub.isBlank()) {
                email = sub + "@google.local";
            } else {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Không lấy được thông tin định danh từ OAuth2 provider"
                );
                return;
            }
        }

        // Nếu không có tên thì dùng login, nếu không có login thì dùng email
        if (name == null || name.isBlank()) {
            if (login != null && !login.isBlank()) {
                name = login;
            } else {
                name = email;
            }
        }

        // Biến dùng trong lambda cần là final/effectively final
        String finalEmail = email;
        String finalName = name;

        /*
         * Tìm user theo email.
         * Nếu chưa có thì tạo user mới với role mặc định là STUDENT.
         */
        Users user = usersRepository.findByEmail(finalEmail)
                .orElseGet(() -> {
                    Users newUser = new Users();

                    newUser.setEmail(finalEmail);
                    newUser.setFullname(finalName);

                    // User OAuth2 không dùng password hệ thống nên tạo password giả đã hash
                    String randomPassword = UUID.randomUUID().toString();
                    String encodedPassword = passwordEncoder.encode(randomPassword);
                    newUser.setPasswordHash(encodedPassword);

                    // Provider không trả gender nên để OTHER
                    newUser.setGender(Gender.OTHER);

                    // Đăng nhập OAuth2 mặc định là học viên
                    newUser.setRole(Role.STUDENT);

                    // Tài khoản được kích hoạt luôn
                    newUser.setStatus(UserStatus.ACTIVE);

                    return usersRepository.save(newUser);
                });

        // Sinh JWT của hệ thống mình
        String token = jwtTokenProvider.generateToken(user);

        // Trả token về trình duyệt
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write(token);
    }
}
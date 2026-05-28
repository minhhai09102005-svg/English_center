package com.example.english_center.service.impl;

import com.example.english_center.dto.LoginRequest;
import com.example.english_center.dto.RegisterRequest;
import com.example.english_center.entity.Users;
import com.example.english_center.enums.Role;
import com.example.english_center.enums.UserStatus;
import com.example.english_center.repository.UsersRepository;
import com.example.english_center.service.AuthService;
import com.example.english_center.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtTokenProvider  jwtTokenProvider;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public String register(RegisterRequest request) {

        if (usersRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email đã tồn tại");
        }

        Users user = new Users();

        user.setFullname(request.getFullname());
        user.setEmail(request.getEmail());

        // Quan trọng nhất: hash password trước khi lưu vào database
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        user.setPhoneNumber(request.getPhoneNumber());
        user.setGender(request.getGender());

        user.setRole(Role.STUDENT);

        user.setStatus(UserStatus.ACTIVE);

        usersRepository.save(user);

        return "Đăng ký thành công";
    }

    @Override
    public String login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Users user = usersRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        return jwtTokenProvider.generateToken(user);
    }
}
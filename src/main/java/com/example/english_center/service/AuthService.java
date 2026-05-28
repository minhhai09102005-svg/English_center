package com.example.english_center.service;

import com.example.english_center.dto.LoginRequest;
import com.example.english_center.dto.RegisterRequest;

public interface AuthService {

    String register(RegisterRequest request);

    String login(LoginRequest request);
}
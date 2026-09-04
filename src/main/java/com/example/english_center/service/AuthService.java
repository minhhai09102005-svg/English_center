package com.example.english_center.service;

import com.example.english_center.dto.request.LoginRequest;

public interface AuthService {

    String login(LoginRequest request);
}
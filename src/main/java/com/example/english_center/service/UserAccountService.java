package com.example.english_center.service;

import com.example.english_center.dto.request.UserAccountRequest;
import com.example.english_center.dto.request.UserAccountUpdateRequest;
import com.example.english_center.dto.response.UserAccountResponse;

import java.util.List;
import java.util.Map;

public interface UserAccountService {

    String createTeacherAccount(UserAccountRequest request);

    String createStudentAccount(UserAccountRequest request);

    List<UserAccountResponse> searchUserAccounts(Map<String, Object> params);

    UserAccountResponse getUserAccountInfo(Long userId);

    UserAccountResponse updateUserAccount(Long userId, UserAccountUpdateRequest request);

    String deactivateUserAccount(Long userId);

    String activateUserAccount(Long userId);
}

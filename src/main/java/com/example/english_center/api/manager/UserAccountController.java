package com.example.english_center.api.manager;

import com.example.english_center.dto.request.UserAccountRequest;
import com.example.english_center.dto.request.UserAccountUpdateRequest;
import com.example.english_center.dto.response.UserAccountResponse;
import com.example.english_center.service.UserAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/manager")
public class UserAccountController {

    private final UserAccountService userAccountService;

    @GetMapping("/users")
    public ResponseEntity<List<UserAccountResponse>> searchUserAccounts(
            @RequestParam Map<String, Object> params
    ) {
        return ResponseEntity.ok(userAccountService.searchUserAccounts(params));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserAccountResponse> getUserAccountInfo(@PathVariable Long id) {
        return ResponseEntity.ok(userAccountService.getUserAccountInfo(id));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserAccountResponse> updateUserAccount(
            @PathVariable Long id,
            @Valid @RequestBody UserAccountUpdateRequest request
    ) {
        return ResponseEntity.ok(userAccountService.updateUserAccount(id, request));
    }

    @PostMapping("/users/teachers")
    public ResponseEntity<String> createTeacherAccount(
            @Valid @RequestBody UserAccountRequest request
    ) {
        return ResponseEntity.ok(userAccountService.createTeacherAccount(request));
    }

    @PostMapping("/users/students")
    public ResponseEntity<String> createStudentAccount(
            @Valid @RequestBody UserAccountRequest request
    ) {
        return ResponseEntity.ok(userAccountService.createStudentAccount(request));
    }

    @PatchMapping("/users/{id}/deactivate")
    public ResponseEntity<String> deactivateUserAccount(@PathVariable Long id) {
        return ResponseEntity.ok(userAccountService.deactivateUserAccount(id));
    }

    @PatchMapping("/users/{id}/activate")
    public ResponseEntity<String> activateUserAccount(@PathVariable Long id) {
        return ResponseEntity.ok(userAccountService.activateUserAccount(id));
    }
}

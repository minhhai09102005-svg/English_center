package com.example.english_center.api;

import com.example.english_center.dto.request.LoginRequest;
import com.example.english_center.dto.response.CourseResponse;
import com.example.english_center.service.AuthService;
import com.example.english_center.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PublicController {

    private final AuthService authService;
    private final CourseService courseService;

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/courses")
    public ResponseEntity<List<CourseResponse>> searchCourses(
            @RequestParam Map<String, Object> params
    ) {
        return ResponseEntity.ok(courseService.searchCourses(params));
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<CourseResponse> getCourseInfo(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseInfo(id));
    }
}

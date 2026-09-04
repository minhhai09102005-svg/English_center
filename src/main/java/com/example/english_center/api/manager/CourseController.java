package com.example.english_center.api.manager;

import com.example.english_center.dto.request.CourseRequest;
import com.example.english_center.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/manager/courses")
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<String> createCourse(
            @Valid @RequestBody CourseRequest request
    ) {
        return ResponseEntity.ok(courseService.createCourse(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequest request
    ) {
        return ResponseEntity.ok(courseService.updateCourse(id, request));
    }
}

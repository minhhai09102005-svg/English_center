package com.example.english_center.api.manager;

import com.example.english_center.dto.request.CourseRequest;
import com.example.english_center.dto.response.CourseResponse;
import com.example.english_center.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/manager/courses")
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/archived")
    public ResponseEntity<List<CourseResponse>> searchArchivedCourses(
            @RequestParam Map<String, Object> params
    ) {
        return ResponseEntity.ok(courseService.searchArchivedCourses(params));
    }

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

    @PatchMapping("/{id}/open")
    public ResponseEntity<String> openCourse(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.openCourse(id));
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<String> closeCourse(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.closeCourse(id));
    }

    @PatchMapping("/{id}/archive")
    public ResponseEntity<String> archiveCourse(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.archiveCourse(id));
    }
}

package com.example.english_center.service;

import com.example.english_center.dto.request.CourseRequest;

public interface CourseService {

    String createCourse(CourseRequest request);

    String updateCourse(Long courseId, CourseRequest request);
}

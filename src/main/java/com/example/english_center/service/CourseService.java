package com.example.english_center.service;

import com.example.english_center.dto.request.CourseRequest;
import com.example.english_center.dto.response.CourseResponse;

import java.util.List;
import java.util.Map;

public interface CourseService {

    List<CourseResponse> searchCourses(Map<String, Object> params);

    List<CourseResponse> searchArchivedCourses(Map<String, Object> params);

    CourseResponse getCourseInfo(Long courseId);

    String createCourse(CourseRequest request);

    String updateCourse(Long courseId, CourseRequest request);

    String openCourse(Long courseId);

    String closeCourse(Long courseId);

    String archiveCourse(Long courseId);
}

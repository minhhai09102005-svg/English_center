package com.example.english_center.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.english_center.entity.Course;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCourseCode(String courseCode);

    Optional<Course> findByCourseName(String courseName);

    boolean existsByCourseCode(String courseCode);

    boolean existsByCourseName(String courseName);
}

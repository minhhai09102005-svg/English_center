package com.example.english_center.service.impl;

import com.example.english_center.customExceptions.ConflictException;
import com.example.english_center.customExceptions.DataNotFoundException;
import com.example.english_center.dto.request.CourseRequest;
import com.example.english_center.entity.Course;
import com.example.english_center.enums.CourseStatus;
import com.example.english_center.repository.CourseRepository;
import com.example.english_center.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public String createCourse(CourseRequest request) {
        String courseCode = request.getCourseCode().trim().toUpperCase();
        String courseName = request.getCourseName().trim();

        if (courseRepository.existsByCourseCode(courseCode)) {
            throw ConflictException.duplicateCourseCode();
        }

        if (courseRepository.existsByCourseName(courseName)) {
            throw ConflictException.duplicateCourseName();
        }

        Course course = new Course();
        course.setCourseCode(courseCode);
        course.setCourseName(courseName);
        String description = request.getDescription();
        course.setDescription(description == null || description.isBlank() ? null : description.trim());
        course.setDurationWeeks(request.getDurationWeeks());
        course.setTuitionFee(request.getTuitionFee());
        course.setStatus(request.getStatus() == null ? CourseStatus.OPEN : request.getStatus());

        courseRepository.save(course);

        return "Đã tạo thành công khoá học mới";
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public String updateCourse(Long courseId, CourseRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(DataNotFoundException::course);
        String courseCode = request.getCourseCode().trim().toUpperCase();
        String courseName = request.getCourseName().trim();

        courseRepository.findByCourseCode(courseCode)
                .filter(existingCourse -> !existingCourse.getId().equals(courseId))
                .ifPresent(existingCourse -> {
                    throw ConflictException.duplicateCourseCode();
                });

        courseRepository.findByCourseName(courseName)
                .filter(existingCourse -> !existingCourse.getId().equals(courseId))
                .ifPresent(existingCourse -> {
                    throw ConflictException.duplicateCourseName();
                });

        course.setCourseCode(courseCode);
        course.setCourseName(courseName);
        String description = request.getDescription();
        course.setDescription(description == null || description.isBlank() ? null : description.trim());
        course.setDurationWeeks(request.getDurationWeeks());
        course.setTuitionFee(request.getTuitionFee());
        if (request.getStatus() != null) {
            course.setStatus(request.getStatus());
        }

        courseRepository.save(course);

        return "Đã sửa thông tin khoá học thành công";
    }
}

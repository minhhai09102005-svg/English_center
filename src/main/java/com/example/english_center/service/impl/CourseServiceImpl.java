package com.example.english_center.service.impl;

import com.example.english_center.builder.CourseSearchBuilder;
import com.example.english_center.converter.SearchBuilderConverter;
import com.example.english_center.customExceptions.ConflictException;
import com.example.english_center.customExceptions.DataNotFoundException;
import com.example.english_center.dto.request.CourseRequest;
import com.example.english_center.dto.response.CourseResponse;
import com.example.english_center.entity.Course;
import com.example.english_center.enums.CourseStatus;
import com.example.english_center.repository.CourseRepository;
import com.example.english_center.service.CourseService;
import com.example.english_center.specification.CourseSpecs;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final SearchBuilderConverter searchBuilderConverter;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> searchCourses(Map<String, Object> params) {
        CourseSearchBuilder courseSearchBuilder = searchBuilderConverter.toCourseSearchBuilder(params);
        var courseSpecs = CourseSpecs.byBuilder(courseSearchBuilder);
        List<Course> courses = courseRepository.findAll(courseSpecs);
        if (courses.isEmpty()) {
            throw DataNotFoundException.course();
        }

        List<CourseResponse> courseResponses = new ArrayList<>();
        for (Course course : courses) {
            courseResponses.add(modelMapper.map(course, CourseResponse.class));
        }
        return courseResponses;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public List<CourseResponse> searchArchivedCourses(Map<String, Object> params) {
        Map<String, Object> archivedParams = new HashMap<>(params);
        archivedParams.put("status", CourseStatus.ARCHIVED.name());

        CourseSearchBuilder courseSearchBuilder = searchBuilderConverter.toCourseSearchBuilder(archivedParams);
        var courseSpecs = CourseSpecs.byBuilder(courseSearchBuilder);
        List<Course> courses = courseRepository.findAll(courseSpecs);
        if (courses.isEmpty()) {
            throw DataNotFoundException.course();
        }

        List<CourseResponse> courseResponses = new ArrayList<>();
        for (Course course : courses) {
            courseResponses.add(modelMapper.map(course, CourseResponse.class));
        }
        return courseResponses;
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getCourseInfo(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(DataNotFoundException::course);
        return modelMapper.map(course, CourseResponse.class);
    }

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

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public String openCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(DataNotFoundException::course);

        course.setStatus(CourseStatus.OPEN);
        courseRepository.save(course);

        return "Đã mở khóa học thành công";
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public String closeCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(DataNotFoundException::course);

        course.setStatus(CourseStatus.CLOSED);
        courseRepository.save(course);

        return "Đã đóng khóa học thành công";
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public String archiveCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(DataNotFoundException::course);

        course.setStatus(CourseStatus.ARCHIVED);
        courseRepository.save(course);

        return "Đã lưu trữ khóa học thành công";
    }
}

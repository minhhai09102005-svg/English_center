package com.example.english_center.dto.response;

import com.example.english_center.enums.CourseStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CourseResponse {

    private Long id;
    private String courseCode;
    private String courseName;
    private String description;
    private Integer durationWeeks;
    private BigDecimal tuitionFee;
    private CourseStatus status;
}

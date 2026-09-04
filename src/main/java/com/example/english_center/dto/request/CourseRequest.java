package com.example.english_center.dto.request;

import com.example.english_center.enums.CourseStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CourseRequest {

    @NotBlank
    private String courseCode;

    @NotBlank
    private String courseName;

    private String description;

    @PositiveOrZero
    private Integer durationWeeks;

    @PositiveOrZero
    private BigDecimal tuitionFee;

    private CourseStatus status;
}

package com.example.english_center.entity;

import com.example.english_center.enums.GradeType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "gradeRecords")
public class GradeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studentId", nullable = false)
    private StudentProfiles student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classId", nullable = false)
    private Course courseClass;

    @Enumerated(EnumType.STRING)
    @Column(name = "gradeType", nullable = false)
    private GradeType gradeType;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "scoreValue", nullable = false, precision = 5, scale = 2)
    private BigDecimal scoreValue;

    @Column(name = "comment", length = 255)
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gradedBy")
    private TeacherProfiles gradedBy;

    @Column(name = "gradedAt")
    private LocalDateTime gradedAt;

    @Column(name = "sentAt")
    private LocalDateTime sentAt;

    @CreationTimestamp
    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;
}
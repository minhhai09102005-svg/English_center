package com.example.english_center.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "studentProfiles")
public class StudentProfiles {

    @Id
    private Long id;

    @Column(name = "studentCode", nullable = false, unique = true, length = 20)
    private String studentCode;

    @Column(name = "dateOfBirth")
    private LocalDate dateOfBirth;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "parentName", length = 150)
    private String parentName;

    @Column(name = "parentPhone", length = 20)
    private String parentPhone;

    @CreationTimestamp
    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    public void generateStudentCode() {
        if (this.id != null) {
            this.studentCode = "S" + this.id;
        }
    }
}
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
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "teacherProfiles")
public class TeacherProfiles {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private Users user;

    @Column(name = "teacherCode", nullable = false, unique = true, length = 20)
    private String teacherCode;

    @Column(name = "specialization", length = 100)
    private String specialization;

    @Column(name = "qualification", length = 150)
    private String qualification;

    @Column(name = "experienceYears")
    private Integer experienceYears;

    @CreationTimestamp
    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "documentFolderId", length = 255)
    private String documentFolderId;

    @Column(name = "documentFolderUrl", length = 1000)
    private String documentFolderUrl;

    public void generateTeacherCode() {
        Long sourceId = this.id;
        if (sourceId == null && this.user != null) {
            sourceId = this.user.getId();
        }
        if (sourceId != null) {
            this.teacherCode = "T" + sourceId;
        }
    }
}

package com.example.english_center.repository;

import com.example.english_center.entity.StudentProfiles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentProfilesRepository extends JpaRepository<StudentProfiles, Long> {
}
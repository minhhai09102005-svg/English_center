package com.example.english_center.repository;

import com.example.english_center.entity.TeacherProfiles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherProfilesRepository extends JpaRepository<TeacherProfiles, Long> {

}

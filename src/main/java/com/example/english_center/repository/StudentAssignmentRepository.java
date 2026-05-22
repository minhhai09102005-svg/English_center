package com.example.english_center.repository;

import com.example.english_center.entity.StudentAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentAssignmentRepository extends JpaRepository<StudentAssignment, Long> {
}
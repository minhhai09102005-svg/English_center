package com.example.english_center.repository;

import com.example.english_center.entity.GradeRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRecordRepository extends JpaRepository<GradeRecord, Long> {
}
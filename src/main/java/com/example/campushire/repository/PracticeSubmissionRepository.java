package com.example.campushire.repository;

import com.example.campushire.entity.PracticeSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PracticeSubmissionRepository extends JpaRepository<PracticeSubmission, Long> {
    List<PracticeSubmission> findByStudentId(Long studentId);
    List<PracticeSubmission> findByStudentIdAndQuestionCategory(Long studentId, String category);
}

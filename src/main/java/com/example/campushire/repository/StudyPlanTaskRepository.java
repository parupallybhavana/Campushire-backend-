package com.example.campushire.repository;

import com.example.campushire.entity.StudyPlanTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyPlanTaskRepository extends JpaRepository<StudyPlanTask, Long> {
    List<StudyPlanTask> findByStudentId(Long studentId);
}

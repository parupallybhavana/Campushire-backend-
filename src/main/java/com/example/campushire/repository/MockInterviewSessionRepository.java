package com.example.campushire.repository;

import com.example.campushire.entity.MockInterviewSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockInterviewSessionRepository extends JpaRepository<MockInterviewSession, Long> {
    List<MockInterviewSession> findByStudentId(Long studentId);
}

package com.example.campushire.repository;

import com.example.campushire.entity.MockTestAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockTestAttemptRepository extends JpaRepository<MockTestAttempt, Long> {
    List<MockTestAttempt> findByStudentId(Long studentId);
}

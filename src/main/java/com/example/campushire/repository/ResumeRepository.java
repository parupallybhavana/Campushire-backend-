package com.example.campushire.repository;

import com.example.campushire.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByStudentId(Long studentId);
    Optional<Resume> findFirstByStudentIdOrderByUploadedAtDesc(Long studentId);
}

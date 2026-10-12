package com.example.campushire.repository;

import com.example.campushire.entity.InterviewExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewExperienceRepository extends JpaRepository<InterviewExperience, Long> {
    List<InterviewExperience> findByCompanyIdAndStatus(Long companyId, String status);
    List<InterviewExperience> findByStatus(String status);
}

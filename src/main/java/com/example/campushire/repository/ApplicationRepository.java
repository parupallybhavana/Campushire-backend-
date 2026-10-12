package com.example.campushire.repository;

import com.example.campushire.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByStudentId(Long studentId);
    List<Application> findByPlacementDriveId(Long driveId);
    boolean existsByStudentIdAndPlacementDriveId(Long studentId, Long driveId);
    Optional<Application> findByStudentIdAndPlacementDriveId(Long studentId, Long driveId);
}

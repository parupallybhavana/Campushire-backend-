package com.example.campushire.repository;

import com.example.campushire.entity.SavedCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedCompanyRepository extends JpaRepository<SavedCompany, Long> {
    List<SavedCompany> findByStudentId(Long studentId);
    Optional<SavedCompany> findByStudentIdAndCompanyId(Long studentId, Long companyId);
    boolean existsByStudentIdAndCompanyId(Long studentId, Long companyId);
    void deleteByStudentIdAndCompanyId(Long studentId, Long companyId);
}

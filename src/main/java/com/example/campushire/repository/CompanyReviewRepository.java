package com.example.campushire.repository;

import com.example.campushire.entity.CompanyReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyReviewRepository extends JpaRepository<CompanyReview, Long> {
    List<CompanyReview> findByCompanyIdAndStatus(Long companyId, String status);
}

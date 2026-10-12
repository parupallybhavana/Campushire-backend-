package com.example.campushire.repository;

import com.example.campushire.entity.MockTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockTestRepository extends JpaRepository<MockTest, Long> {
    List<MockTest> findByCategory(String category);
}

package com.example.campushire.repository;

import com.example.campushire.entity.PracticeQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PracticeQuestionRepository extends JpaRepository<PracticeQuestion, Long> {
    List<PracticeQuestion> findByCategory(String category);
    List<PracticeQuestion> findByCategoryAndDifficulty(String category, String difficulty);
    List<PracticeQuestion> findByTopicContainingIgnoreCase(String topic);
}

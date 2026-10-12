package com.example.campushire.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mock_interview_sessions")
public class MockInterviewSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    private String interviewType; // TECHNICAL, CODING, HR, PROJECT, COMPANY_SPECIFIC

    private String targetRole;

    private String difficulty; // BEGINNER, INTERMEDIATE, ADVANCED

    private LocalDateTime scheduledTime;

    private Integer durationMinutes;

    private String status = "SCHEDULED"; // SCHEDULED, COMPLETED, CANCELLED

    private Integer score;

    @Column(columnDefinition = "TEXT")
    private String feedbackText;

    @Column(columnDefinition = "TEXT")
    private String qaJson;

    public MockInterviewSession() {
    }

    public MockInterviewSession(Long id, Student student, String interviewType, String targetRole, String difficulty, LocalDateTime scheduledTime, Integer durationMinutes, String status) {
        this.id = id;
        this.student = student;
        this.interviewType = interviewType;
        this.targetRole = targetRole;
        this.difficulty = difficulty;
        this.scheduledTime = scheduledTime;
        this.durationMinutes = durationMinutes;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getInterviewType() {
        return interviewType;
    }

    public void setInterviewType(String interviewType) {
        this.interviewType = interviewType;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }

    public String getQaJson() {
        return qaJson;
    }

    public void setQaJson(String qaJson) {
        this.qaJson = qaJson;
    }
}

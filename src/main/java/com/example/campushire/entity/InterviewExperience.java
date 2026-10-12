package com.example.campushire.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "interview_experiences")
public class InterviewExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false)
    private String title;

    private String roleTitle;

    private String difficulty; // EASY, MEDIUM, HARD

    @Column(columnDefinition = "TEXT")
    private String roundDetails;

    @Column(columnDefinition = "TEXT")
    private String questionsAsked;

    @Column(columnDefinition = "TEXT")
    private String preparationTips;

    private String status = "APPROVED"; // PENDING, APPROVED, REJECTED

    private LocalDateTime createdAt = LocalDateTime.now();

    public InterviewExperience() {
    }

    public InterviewExperience(Long id, Student student, Company company, String title, String roleTitle, String difficulty, String roundDetails, String questionsAsked, String preparationTips) {
        this.id = id;
        this.student = student;
        this.company = company;
        this.title = title;
        this.roleTitle = roleTitle;
        this.difficulty = difficulty;
        this.roundDetails = roundDetails;
        this.questionsAsked = questionsAsked;
        this.preparationTips = preparationTips;
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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRoleTitle() {
        return roleTitle;
    }

    public void setRoleTitle(String roleTitle) {
        this.roleTitle = roleTitle;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getRoundDetails() {
        return roundDetails;
    }

    public void setRoundDetails(String roundDetails) {
        this.roundDetails = roundDetails;
    }

    public String getQuestionsAsked() {
        return questionsAsked;
    }

    public void setQuestionsAsked(String questionsAsked) {
        this.questionsAsked = questionsAsked;
    }

    public String getPreparationTips() {
        return preparationTips;
    }

    public void setPreparationTips(String preparationTips) {
        this.preparationTips = preparationTips;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

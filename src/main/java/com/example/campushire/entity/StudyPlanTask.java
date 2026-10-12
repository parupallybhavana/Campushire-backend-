package com.example.campushire.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "study_plan_tasks")
public class StudyPlanTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private String title;

    private String category; // CODING, APTITUDE, TECHNICAL, HR, REVISION

    private String dayOfWeek; // MONDAY, TUESDAY, etc.

    private LocalDate scheduledDate;

    private Boolean completed = false;

    @Column(length = 1000)
    private String notes;

    public StudyPlanTask() {
    }

    public StudyPlanTask(Long id, Student student, String title, String category, String dayOfWeek, LocalDate scheduledDate, Boolean completed) {
        this.id = id;
        this.student = student;
        this.title = title;
        this.category = category;
        this.dayOfWeek = dayOfWeek;
        this.scheduledDate = scheduledDate;
        this.completed = completed;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public Boolean getCompleted() {
        return completed != null ? completed : false;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

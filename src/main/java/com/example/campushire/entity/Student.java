package com.example.campushire.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String branch;

    private String degree;

    private Double cgpa;

    private Integer graduationYear;

    private Integer backlogs = 0;

    @Column(length = 1000)
    private String skills;

    @Column(length = 3000)
    private String projects;

    @Column(length = 3000)
    private String certifications;

    private String phone;

    private String avatarUrl;

    private Integer streakDays = 1;

    private Integer totalXp = 50;

    private Integer solvedCodingCount = 0;

    private Integer solvedAptitudeCount = 0;

    private Integer completedMockCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.ROLE_STUDENT;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applications = new ArrayList<>();

    public Student() {
    }

    public Student(Long id, String name, String email, String password, String branch, Double cgpa, Integer graduationYear, String skills, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.branch = branch;
        this.cgpa = cgpa;
        this.graduationYear = graduationYear;
        this.skills = skills;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public Integer getBacklogs() {
        return backlogs != null ? backlogs : 0;
    }

    public void setBacklogs(Integer backlogs) {
        this.backlogs = backlogs;
    }

    public String getProjects() {
        return projects;
    }

    public void setProjects(String projects) {
        this.projects = projects;
    }

    public String getCertifications() {
        return certifications;
    }

    public void setCertifications(String certifications) {
        this.certifications = certifications;
    }

    public Double getCgpa() {
        return cgpa;
    }

    public void setCgpa(Double cgpa) {
        this.cgpa = cgpa;
    }

    public Integer getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(Integer graduationYear) {
        this.graduationYear = graduationYear;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Integer getStreakDays() {
        return streakDays != null ? streakDays : 1;
    }

    public void setStreakDays(Integer streakDays) {
        this.streakDays = streakDays;
    }

    public Integer getTotalXp() {
        return totalXp != null ? totalXp : 0;
    }

    public void setTotalXp(Integer totalXp) {
        this.totalXp = totalXp;
    }

    public Integer getSolvedCodingCount() {
        return solvedCodingCount != null ? solvedCodingCount : 0;
    }

    public void setSolvedCodingCount(Integer solvedCodingCount) {
        this.solvedCodingCount = solvedCodingCount;
    }

    public Integer getSolvedAptitudeCount() {
        return solvedAptitudeCount != null ? solvedAptitudeCount : 0;
    }

    public void setSolvedAptitudeCount(Integer solvedAptitudeCount) {
        this.solvedAptitudeCount = solvedAptitudeCount;
    }

    public Integer getCompletedMockCount() {
        return completedMockCount != null ? completedMockCount : 0;
    }

    public void setCompletedMockCount(Integer completedMockCount) {
        this.completedMockCount = completedMockCount;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Application> getApplications() {
        return applications;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }
}

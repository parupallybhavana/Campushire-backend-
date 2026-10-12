package com.example.campushire.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "placement_drives")
public class PlacementDrive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    private Double minCgpa;

    @Column(length = 1000)
    private String requiredSkills;

    private String location;

    private String salaryCtc;

    private String eligibleBranches;

    private String jobRole;

    private String experienceReq;

    private Integer minGraduationYear;

    private Integer backlogLimit;

    @Column(length = 2000)
    private String recruitmentStages;

    private LocalDate deadline;

    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @OneToMany(mappedBy = "placementDrive", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applications = new ArrayList<>();

    public PlacementDrive() {
    }

    public PlacementDrive(Long id, String title, String description, Double minCgpa, String requiredSkills, String location, LocalDate deadline, Company company) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.minCgpa = minCgpa;
        this.requiredSkills = requiredSkills;
        this.location = location;
        this.deadline = deadline;
        this.company = company;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getMinCgpa() {
        return minCgpa;
    }

    public void setMinCgpa(Double minCgpa) {
        this.minCgpa = minCgpa;
    }

    public String getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(String requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSalaryCtc() {
        return salaryCtc;
    }

    public void setSalaryCtc(String salaryCtc) {
        this.salaryCtc = salaryCtc;
    }

    public String getEligibleBranches() {
        return eligibleBranches;
    }

    public void setEligibleBranches(String eligibleBranches) {
        this.eligibleBranches = eligibleBranches;
    }

    public String getJobRole() {
        return jobRole;
    }

    public void setJobRole(String jobRole) {
        this.jobRole = jobRole;
    }

    public String getExperienceReq() {
        return experienceReq;
    }

    public void setExperienceReq(String experienceReq) {
        this.experienceReq = experienceReq;
    }

    public Integer getMinGraduationYear() {
        return minGraduationYear;
    }

    public void setMinGraduationYear(Integer minGraduationYear) {
        this.minGraduationYear = minGraduationYear;
    }

    public Integer getBacklogLimit() {
        return backlogLimit;
    }

    public void setBacklogLimit(Integer backlogLimit) {
        this.backlogLimit = backlogLimit;
    }

    public String getRecruitmentStages() {
        return recruitmentStages;
    }

    public void setRecruitmentStages(String recruitmentStages) {
        this.recruitmentStages = recruitmentStages;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public Boolean getIsActive() {
        return isActive != null ? isActive : true;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public List<Application> getApplications() {
        return applications;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }
}

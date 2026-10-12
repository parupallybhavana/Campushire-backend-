package com.example.campushire.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class PlacementDriveDTO {

    private Long id;

    @NotBlank(message = "Job title is required")
    private String title;

    private String description;
    private Double minCgpa;
    private String requiredSkills;
    private String location;
    private LocalDate deadline;

    @NotNull(message = "Company ID is required")
    private Long companyId;

    private String companyName;
    private String companyLogo;
    private String salaryCtc;
    private String eligibleBranches;
    private String jobRole;
    private String experienceReq;
    private Integer minGraduationYear;
    private Integer backlogLimit;
    private String recruitmentStages;
    private Boolean isActive = true;
    private Integer applicationsCount = 0;

    public PlacementDriveDTO() {
    }

    public PlacementDriveDTO(Long id, String title, String description, Double minCgpa, String requiredSkills, String location, LocalDate deadline, Long companyId, String companyName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.minCgpa = minCgpa;
        this.requiredSkills = requiredSkills;
        this.location = location;
        this.deadline = deadline;
        this.companyId = companyId;
        this.companyName = companyName;
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

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
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

    public Boolean getIsActive() {
        return isActive != null ? isActive : true;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getApplicationsCount() {
        return applicationsCount;
    }

    public void setApplicationsCount(Integer applicationsCount) {
        this.applicationsCount = applicationsCount;
    }
}

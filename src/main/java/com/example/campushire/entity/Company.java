package com.example.campushire.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String location;

    private String website;

    private String logoUrl;

    private String industry;

    private String headquarters;

    private String companySize;

    private String ctcPackage;

    @Column(columnDefinition = "TEXT")
    private String selectionProcess;

    @Column(columnDefinition = "TEXT")
    private String techTopics;

    @Column(columnDefinition = "TEXT")
    private String aptitudeTopics;

    @Column(columnDefinition = "TEXT")
    private String hrQuestions;

    @Column(columnDefinition = "TEXT")
    private String prepChecklist;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlacementDrive> drives = new ArrayList<>();

    public Company() {
    }

    public Company(Long id, String name, String description, String location, String website) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.website = website;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getHeadquarters() {
        return headquarters;
    }

    public void setHeadquarters(String headquarters) {
        this.headquarters = headquarters;
    }

    public String getCompanySize() {
        return companySize;
    }

    public void setCompanySize(String companySize) {
        this.companySize = companySize;
    }

    public String getCtcPackage() {
        return ctcPackage;
    }

    public void setCtcPackage(String ctcPackage) {
        this.ctcPackage = ctcPackage;
    }

    public String getSelectionProcess() {
        return selectionProcess;
    }

    public void setSelectionProcess(String selectionProcess) {
        this.selectionProcess = selectionProcess;
    }

    public String getTechTopics() {
        return techTopics;
    }

    public void setTechTopics(String techTopics) {
        this.techTopics = techTopics;
    }

    public String getAptitudeTopics() {
        return aptitudeTopics;
    }

    public void setAptitudeTopics(String aptitudeTopics) {
        this.aptitudeTopics = aptitudeTopics;
    }

    public String getHrQuestions() {
        return hrQuestions;
    }

    public void setHrQuestions(String hrQuestions) {
        this.hrQuestions = hrQuestions;
    }

    public String getPrepChecklist() {
        return prepChecklist;
    }

    public void setPrepChecklist(String prepChecklist) {
        this.prepChecklist = prepChecklist;
    }

    public List<PlacementDrive> getDrives() {
        return drives;
    }

    public void setDrives(List<PlacementDrive> drives) {
        this.drives = drives;
    }
}

package com.example.campushire.dto;

import java.time.LocalDateTime;

public class ApplicationDTO {

    private Long id;
    private StudentDTO student;
    private PlacementDriveDTO placementDrive;
    private LocalDateTime appliedDate;
    private String status;

    public ApplicationDTO() {
    }

    public ApplicationDTO(Long id, StudentDTO student, PlacementDriveDTO placementDrive, LocalDateTime appliedDate, String status) {
        this.id = id;
        this.student = student;
        this.placementDrive = placementDrive;
        this.appliedDate = appliedDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StudentDTO getStudent() {
        return student;
    }

    public void setStudent(StudentDTO student) {
        this.student = student;
    }

    public PlacementDriveDTO getPlacementDrive() {
        return placementDrive;
    }

    public void setPlacementDrive(PlacementDriveDTO placementDrive) {
        this.placementDrive = placementDrive;
    }

    public LocalDateTime getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(LocalDateTime appliedDate) {
        this.appliedDate = appliedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

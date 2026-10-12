package com.example.campushire.service;

import com.example.campushire.dto.ApplicationDTO;
import com.example.campushire.dto.PlacementDriveDTO;
import com.example.campushire.dto.StudentDTO;
import com.example.campushire.entity.Application;
import com.example.campushire.entity.ApplicationStatus;
import com.example.campushire.entity.Notification;
import com.example.campushire.entity.PlacementDrive;
import com.example.campushire.entity.Student;
import com.example.campushire.exception.BadRequestException;
import com.example.campushire.exception.ResourceNotFoundException;
import com.example.campushire.repository.ApplicationRepository;
import com.example.campushire.repository.NotificationRepository;
import com.example.campushire.repository.PlacementDriveRepository;
import com.example.campushire.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PlacementDriveRepository driveRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private StudentService studentService;

    @Autowired
    private PlacementDriveService driveService;

    @Transactional
    public ApplicationDTO applyForJob(String studentEmail, Long jobId) {
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        PlacementDrive drive = driveRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Placement Drive not found with id: " + jobId));

        // 1. Check if drive is active
        if (Boolean.FALSE.equals(drive.getIsActive())) {
            throw new BadRequestException("This placement drive is currently inactive and not accepting applications.");
        }

        // 2. Check deadline
        if (drive.getDeadline() != null && LocalDate.now().isAfter(drive.getDeadline())) {
            throw new BadRequestException("Application deadline (" + drive.getDeadline() + ") for this placement drive has already passed.");
        }

        // 3. Check duplicate application
        if (applicationRepository.existsByStudentIdAndPlacementDriveId(student.getId(), drive.getId())) {
            throw new BadRequestException("You have already applied for this placement drive.");
        }

        // 4. Check eligibility - CGPA
        if (drive.getMinCgpa() != null) {
            if (student.getCgpa() == null || student.getCgpa() < drive.getMinCgpa()) {
                throw new BadRequestException("Ineligible due to CGPA: Minimum required is " + drive.getMinCgpa() + ", but your CGPA is " + (student.getCgpa() != null ? student.getCgpa() : "not provided") + ".");
            }
        }

        // 5. Check eligibility - Backlogs
        if (drive.getBacklogLimit() != null && student.getBacklogs() != null) {
            if (student.getBacklogs() > drive.getBacklogLimit()) {
                throw new BadRequestException("Ineligible due to active backlogs: Maximum allowed backlogs is " + drive.getBacklogLimit() + ", but you have " + student.getBacklogs() + ".");
            }
        }

        // 6. Check eligibility - Graduation Year
        if (drive.getMinGraduationYear() != null && student.getGraduationYear() != null) {
            if (student.getGraduationYear() < drive.getMinGraduationYear()) {
                throw new BadRequestException("Ineligible due to Graduation Year: Drive is for " + drive.getMinGraduationYear() + " batch or later (Your batch: " + student.getGraduationYear() + ").");
            }
        }

        // 7. Check eligibility - Branch
        if (drive.getEligibleBranches() != null && !drive.getEligibleBranches().isBlank() && !drive.getEligibleBranches().equalsIgnoreCase("All")) {
            if (student.getBranch() != null && !student.getBranch().isBlank()) {
                String allowed = drive.getEligibleBranches().toLowerCase();
                String sBranch = student.getBranch().toLowerCase();
                boolean branchMatches = allowed.contains(sBranch) || sBranch.contains(allowed) 
                        || (sBranch.contains("cse") && (allowed.contains("cse") || allowed.contains("computer")))
                        || (sBranch.contains("computer") && (allowed.contains("cse") || allowed.contains("computer")))
                        || (sBranch.contains("it") && allowed.contains("it"))
                        || (sBranch.contains("ece") && allowed.contains("ece"));
                if (!branchMatches && !allowed.contains("all branches") && !allowed.contains("any")) {
                    throw new BadRequestException("Ineligible due to Branch: Eligible branches are [" + drive.getEligibleBranches() + "], but your branch is [" + student.getBranch() + "].");
                }
            }
        }

        Application application = new Application();
        application.setStudent(student);
        application.setPlacementDrive(drive);
        application.setAppliedDate(LocalDateTime.now());
        application.setStatus(ApplicationStatus.APPLIED);

        Application saved = applicationRepository.save(application);

        // Send confirmation notification
        try {
            Notification notif = new Notification();
            notif.setStudent(student);
            notif.setTitle("Application Submitted: " + drive.getTitle());
            notif.setMessage("You have successfully submitted your application for " + drive.getTitle() + " at " + (drive.getCompany() != null ? drive.getCompany().getName() : "the company") + ".");
            notif.setLinkUrl("/student/applications");
            notif.setType("APPLICATION_UPDATE");
            notificationRepository.save(notif);
        } catch (Exception e) {
            // Ignore notification failure
        }

        return mapToDTO(saved);
    }

    public List<ApplicationDTO> getMyApplications(String studentEmail) {
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        return applicationRepository.findByStudentId(student.getId()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ApplicationDTO> getAllApplications() {
        return applicationRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ApplicationDTO updateStatus(Long applicationId, String statusStr) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        try {
            String norm = statusStr.trim().toUpperCase().replace(" ", "_");
            ApplicationStatus status = ApplicationStatus.valueOf(norm);
            application.setStatus(status);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status: " + statusStr + ". Valid statuses are: APPLIED, UNDER_REVIEW, SHORTLISTED, ASSESSMENT, INTERVIEW, SELECTED, REJECTED, WITHDRAWN");
        }

        Application updated = applicationRepository.save(application);

        // Send notification to student on status update
        try {
            Student student = updated.getStudent();
            PlacementDrive drive = updated.getPlacementDrive();
            if (student != null && drive != null) {
                Notification notif = new Notification();
                notif.setStudent(student);
                notif.setTitle("Application Status Update: " + drive.getTitle());
                notif.setMessage("Your application status for " + drive.getTitle() + " at " + (drive.getCompany() != null ? drive.getCompany().getName() : "Company") + " is now updated to: " + updated.getStatus().name() + ".");
                notif.setLinkUrl("/student/applications");
                notif.setType("APPLICATION_UPDATE");
                notificationRepository.save(notif);
            }
        } catch (Exception e) {
            // Ignore notification failure
        }

        return mapToDTO(updated);
    }

    public ApplicationDTO mapToDTO(Application app) {
        StudentDTO studentDTO = studentService.mapToDTO(app.getStudent());
        PlacementDriveDTO driveDTO = driveService.mapToDTO(app.getPlacementDrive());
        return new ApplicationDTO(
                app.getId(),
                studentDTO,
                driveDTO,
                app.getAppliedDate(),
                app.getStatus().name()
        );
    }
}


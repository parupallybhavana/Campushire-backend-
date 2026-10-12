package com.example.campushire.service;

import com.example.campushire.dto.PlacementDriveDTO;
import com.example.campushire.entity.Company;
import com.example.campushire.entity.Notification;
import com.example.campushire.entity.PlacementDrive;
import com.example.campushire.entity.Student;
import com.example.campushire.exception.ResourceNotFoundException;
import com.example.campushire.repository.CompanyRepository;
import com.example.campushire.repository.NotificationRepository;
import com.example.campushire.repository.PlacementDriveRepository;
import com.example.campushire.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PlacementDriveService {

    @Autowired
    private PlacementDriveRepository driveRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Transactional
    public PlacementDriveDTO createDrive(PlacementDriveDTO dto) {
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + dto.getCompanyId()));

        PlacementDrive drive = new PlacementDrive();
        drive.setTitle(dto.getTitle());
        drive.setDescription(dto.getDescription());
        drive.setMinCgpa(dto.getMinCgpa());
        drive.setRequiredSkills(dto.getRequiredSkills());
        drive.setLocation(dto.getLocation());
        drive.setDeadline(dto.getDeadline());
        drive.setSalaryCtc(dto.getSalaryCtc());
        drive.setEligibleBranches(dto.getEligibleBranches());
        drive.setJobRole(dto.getJobRole() != null ? dto.getJobRole() : dto.getTitle());
        drive.setExperienceReq(dto.getExperienceReq());
        drive.setMinGraduationYear(dto.getMinGraduationYear());
        drive.setBacklogLimit(dto.getBacklogLimit());
        drive.setRecruitmentStages(dto.getRecruitmentStages());
        drive.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        drive.setCompany(company);

        PlacementDrive saved = driveRepository.save(drive);

        // Broadcast notification to all students about the new placement drive
        try {
            List<Student> students = studentRepository.findAll();
            for (Student s : students) {
                if ("ROLE_STUDENT".equalsIgnoreCase(s.getRole().name())) {
                    Notification notif = new Notification();
                    notif.setStudent(s);
                    notif.setTitle("New Placement Drive: " + saved.getTitle());
                    notif.setMessage(company.getName() + " has announced a new recruitment drive: " + saved.getTitle() + ". Apply before " + (saved.getDeadline() != null ? saved.getDeadline() : "the deadline") + ".");
                    notif.setLinkUrl("/student/jobs");
                    notif.setType("NEW_DRIVE");
                    notificationRepository.save(notif);
                }
            }
        } catch (Exception e) {
            // Ignore notification failure during drive creation
        }

        return mapToDTO(saved);
    }

    public List<PlacementDriveDTO> getAllDrives() {
        return driveRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PlacementDriveDTO> getActiveDrives() {
        return driveRepository.findAll().stream()
                .filter(d -> Boolean.TRUE.equals(d.getIsActive()))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public PlacementDriveDTO getDriveById(Long id) {
        PlacementDrive drive = driveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Placement Drive not found with id: " + id));
        return mapToDTO(drive);
    }

    @Transactional
    public PlacementDriveDTO updateDrive(Long id, PlacementDriveDTO dto) {
        PlacementDrive drive = driveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Placement Drive not found with id: " + id));

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + dto.getCompanyId()));

        drive.setTitle(dto.getTitle());
        drive.setDescription(dto.getDescription());
        drive.setMinCgpa(dto.getMinCgpa());
        drive.setRequiredSkills(dto.getRequiredSkills());
        drive.setLocation(dto.getLocation());
        drive.setDeadline(dto.getDeadline());
        drive.setSalaryCtc(dto.getSalaryCtc());
        drive.setEligibleBranches(dto.getEligibleBranches());
        drive.setJobRole(dto.getJobRole() != null ? dto.getJobRole() : dto.getTitle());
        drive.setExperienceReq(dto.getExperienceReq());
        drive.setMinGraduationYear(dto.getMinGraduationYear());
        drive.setBacklogLimit(dto.getBacklogLimit());
        drive.setRecruitmentStages(dto.getRecruitmentStages());
        if (dto.getIsActive() != null) {
            drive.setIsActive(dto.getIsActive());
        }
        drive.setCompany(company);

        PlacementDrive updated = driveRepository.save(drive);
        return mapToDTO(updated);
    }

    @Transactional
    public PlacementDriveDTO toggleDriveStatus(Long id) {
        PlacementDrive drive = driveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Placement Drive not found with id: " + id));
        drive.setIsActive(!Boolean.TRUE.equals(drive.getIsActive()));
        PlacementDrive updated = driveRepository.save(drive);
        return mapToDTO(updated);
    }

    @Transactional
    public void deleteDrive(Long id) {
        if (!driveRepository.existsById(id)) {
            throw new ResourceNotFoundException("Placement Drive not found with id: " + id);
        }
        driveRepository.deleteById(id);
    }

    public PlacementDriveDTO mapToDTO(PlacementDrive drive) {
        PlacementDriveDTO dto = new PlacementDriveDTO(
                drive.getId(),
                drive.getTitle(),
                drive.getDescription(),
                drive.getMinCgpa(),
                drive.getRequiredSkills(),
                drive.getLocation(),
                drive.getDeadline(),
                drive.getCompany() != null ? drive.getCompany().getId() : null,
                drive.getCompany() != null ? drive.getCompany().getName() : "Unknown"
        );
        dto.setCompanyLogo(drive.getCompany() != null ? drive.getCompany().getLogoUrl() : null);
        dto.setSalaryCtc(drive.getSalaryCtc());
        dto.setEligibleBranches(drive.getEligibleBranches());
        dto.setJobRole(drive.getJobRole());
        dto.setExperienceReq(drive.getExperienceReq());
        dto.setMinGraduationYear(drive.getMinGraduationYear());
        dto.setBacklogLimit(drive.getBacklogLimit());
        dto.setRecruitmentStages(drive.getRecruitmentStages());
        dto.setIsActive(drive.getIsActive());
        dto.setApplicationsCount(drive.getApplications() != null ? drive.getApplications().size() : 0);
        return dto;
    }
}


package com.example.campushire.controller;

import com.example.campushire.dto.StudentDTO;
import com.example.campushire.entity.*;
import com.example.campushire.repository.*;
import com.example.campushire.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
@Transactional(readOnly = true)
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PlacementDriveRepository driveRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private PracticeSubmissionRepository submissionRepository;

    @GetMapping("/profile")
    public ResponseEntity<StudentDTO> getProfile(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(studentService.getProfile(email));
    }

    @PutMapping("/profile")
    public ResponseEntity<StudentDTO> updateProfile(Authentication authentication, @RequestBody StudentDTO dto) {
        String email = authentication.getName();
        return ResponseEntity.ok(studentService.updateProfile(email, dto));
    }

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private MockTestAttemptRepository mockTestAttemptRepository;

    @GetMapping("/dashboard-summary")
    public ResponseEntity<?> getDashboardSummary(Authentication authentication) {
        String email = authentication.getName();
        Optional<Student> studentOpt = studentRepository.findByEmail(email);
        if (studentOpt.isEmpty()) return ResponseEntity.notFound().build();

        Student student = studentOpt.get();

        List<PlacementDrive> allDrives = driveRepository.findAll();
        long availableDrivesCount = allDrives.stream().filter(d -> d.getIsActive() == null || d.getIsActive()).count();

        long eligibleCompaniesCount = allDrives.stream()
                .filter(d -> (d.getIsActive() == null || d.getIsActive()) && (student.getCgpa() == null || d.getMinCgpa() == null || student.getCgpa() >= d.getMinCgpa()))
                .map(d -> d.getCompany().getId())
                .distinct().count();

        List<Application> studentApps = applicationRepository.findByStudentId(student.getId());
        long totalAppsCount = studentApps.size();
        long activeAppsCount = studentApps.stream()
                .filter(a -> a.getStatus() != ApplicationStatus.REJECTED && a.getStatus() != ApplicationStatus.SELECTED)
                .count();
        long shortlistedCount = studentApps.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.SHORTLISTED || a.getStatus() == ApplicationStatus.ASSESSMENT || a.getStatus() == ApplicationStatus.INTERVIEW || a.getStatus() == ApplicationStatus.SELECTED)
                .count();

        int completedPracticeCount = student.getSolvedCodingCount() + student.getSolvedAptitudeCount();

        // Get latest mock test score
        List<MockTestAttempt> attempts = mockTestAttemptRepository.findByStudentId(student.getId());
        Integer latestMockTestScore = null;
        if (!attempts.isEmpty()) {
            MockTestAttempt latest = attempts.get(attempts.size() - 1);
            if (latest.getPercentage() != null) {
                latestMockTestScore = (int) Math.round(latest.getPercentage());
            } else if (latest.getTotalMarks() != null && latest.getTotalMarks() > 0 && latest.getScore() != null) {
                latestMockTestScore = (int) Math.round((double) latest.getScore() / latest.getTotalMarks() * 100);
            }
        }

        // Calculate dynamic Placement Readiness Score
        int cgpaScore = student.getCgpa() != null ? (int) (student.getCgpa() * 8.5) : 60;
        int codingBonus = Math.min(25, student.getSolvedCodingCount() * 2);
        int mockBonus = latestMockTestScore != null ? (int) (latestMockTestScore * 0.15) : Math.min(15, student.getCompletedMockCount() * 5);
        int readinessScore = Math.min(98, (int) (cgpaScore * 0.4 + codingBonus + mockBonus + 20));

        // Recent Notifications
        List<Notification> notifs = notificationRepository.findByStudentIdOrderByCreatedAtDesc(student.getId());
        List<Map<String, Object>> recentNotifications = notifs.stream().limit(5).map(n -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", n.getId());
            m.put("title", n.getTitle());
            m.put("message", n.getMessage());
            m.put("type", n.getType() != null ? n.getType() : "GENERAL");
            m.put("isRead", n.getIsRead());
            m.put("createdAt", n.getCreatedAt());
            return m;
        }).collect(Collectors.toList());

        // Upcoming Deadlines
        List<Map<String, Object>> upcomingDeadlines = allDrives.stream()
                .filter(d -> (d.getIsActive() == null || d.getIsActive()) && d.getDeadline() != null)
                .sorted(Comparator.comparing(PlacementDrive::getDeadline))
                .limit(4)
                .map(d -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", d.getId());
                    m.put("title", d.getTitle());
                    m.put("companyName", d.getCompany().getName());
                    m.put("deadline", d.getDeadline());
                    m.put("salaryCtc", d.getSalaryCtc());
                    return m;
                }).collect(Collectors.toList());

        Map<String, Object> summary = new HashMap<>();
        summary.put("studentName", student.getName());
        summary.put("email", student.getEmail());
        summary.put("branch", student.getBranch());
        summary.put("cgpa", student.getCgpa());
        summary.put("availableDrivesCount", availableDrivesCount);
        summary.put("eligibleCompaniesCount", eligibleCompaniesCount);
        summary.put("totalApplicationsCount", totalAppsCount);
        summary.put("activeApplicationsCount", activeAppsCount);
        summary.put("shortlistedApplicationsCount", shortlistedCount);
        summary.put("upcomingAssessmentsCount", shortlistedCount > 0 ? shortlistedCount : 0);
        summary.put("practiceTestsCompleted", completedPracticeCount);
        summary.put("readinessScore", readinessScore);
        summary.put("latestMockTestScore", latestMockTestScore != null ? latestMockTestScore : 0);
        summary.put("streakDays", student.getStreakDays());
        summary.put("totalXp", student.getTotalXp());
        summary.put("recentNotifications", recentNotifications);
        summary.put("upcomingDeadlines", upcomingDeadlines);

        // Recommended Drives
        List<Map<String, Object>> recommendedDrives = allDrives.stream()
                .filter(d -> (d.getIsActive() == null || d.getIsActive()) && (student.getCgpa() == null || d.getMinCgpa() == null || student.getCgpa() >= d.getMinCgpa()))
                .limit(4)
                .map(d -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", d.getId());
                    m.put("title", d.getTitle());
                    m.put("companyName", d.getCompany().getName());
                    m.put("companyLogo", d.getCompany().getLogoUrl());
                    m.put("salaryCtc", d.getSalaryCtc() != null ? d.getSalaryCtc() : "Standard Package");
                    m.put("location", d.getLocation());
                    m.put("deadline", d.getDeadline());
                    m.put("minCgpa", d.getMinCgpa());
                    return m;
                }).collect(Collectors.toList());

        summary.put("recommendedDrives", recommendedDrives);

        // Tracker Apps
        List<Map<String, Object>> appTracker = studentApps.stream().map(a -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", a.getId());
            m.put("driveTitle", a.getPlacementDrive().getTitle());
            m.put("companyName", a.getPlacementDrive().getCompany().getName());
            m.put("companyLogo", a.getPlacementDrive().getCompany().getLogoUrl());
            m.put("status", a.getStatus().name());
            m.put("appliedDate", a.getAppliedDate());
            return m;
        }).collect(Collectors.toList());

        summary.put("applicationTracker", appTracker);

        return ResponseEntity.ok(summary);
    }

    @GetMapping("/skill-gap")
    public ResponseEntity<?> getSkillGapAnalysis(
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "Java Developer") String targetRole) {

        String email = authentication.getName();
        Optional<Student> studentOpt = studentRepository.findByEmail(email);
        if (studentOpt.isEmpty()) return ResponseEntity.notFound().build();

        Student student = studentOpt.get();
        String declaredSkills = student.getSkills() != null ? student.getSkills().toLowerCase() : "";

        List<Map<String, Object>> skillsMatrix = new ArrayList<>();

        String[][] requiredSkills = {
                {"Java Core & OOP", "Advanced", declaredSkills.contains("java") ? "Intermediate" : "Beginner", "Practice OOP design questions in Practice Zone"},
                {"Spring Boot & REST APIs", "Intermediate", declaredSkills.contains("spring") ? "Intermediate" : "Basic", "Revise @RestController and JPA annotations"},
                {"SQL & Database Indexing", "Intermediate", declaredSkills.contains("sql") ? "Intermediate" : "Beginner", "Solve 5 SQL join and group by queries"},
                {"Data Structures & Algorithms", "Advanced", student.getSolvedCodingCount() > 10 ? "Intermediate" : "Beginner", "Complete Array and Tree modules"},
                {"Aptitude & Problem Solving", "Advanced", student.getSolvedAptitudeCount() > 20 ? "Advanced" : "Intermediate", "Attempt timed mock assessment"}
        };

        for (String[] row : requiredSkills) {
            Map<String, Object> m = new HashMap<>();
            m.put("skill", row[0]);
            m.put("requiredLevel", row[1]);
            m.put("studentLevel", row[2]);
            m.put("recommendation", row[3]);
            m.put("isMatch", !row[2].equals("Beginner"));
            skillsMatrix.add(m);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("targetRole", targetRole);
        result.put("overallSkillMatchPercentage", 78);
        result.put("skillsMatrix", skillsMatrix);

        return ResponseEntity.ok(result);
    }
}

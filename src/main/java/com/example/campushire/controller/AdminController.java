package com.example.campushire.controller;

import com.example.campushire.dto.*;
import com.example.campushire.entity.*;
import com.example.campushire.repository.*;
import com.example.campushire.service.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@Transactional(readOnly = true)
public class AdminController {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private PlacementDriveService driveService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PlacementDriveRepository driveRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private PracticeQuestionRepository questionRepository;

    @Autowired
    private MockTestRepository mockTestRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private StudentService studentService;

    // --- Admin Dashboard Stats ---
    @GetMapping("/dashboard-stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        long totalStudents = studentRepository.findAll().stream().filter(s -> s.getRole() == Role.ROLE_STUDENT).count();
        long totalCompanies = companyRepository.count();
        long activeDrives = driveRepository.findAll().stream().filter(d -> Boolean.TRUE.equals(d.getIsActive())).count();
        List<Application> apps = applicationRepository.findAll();
        long totalApplications = apps.size();

        long shortlisted = apps.stream().filter(a -> a.getStatus() == ApplicationStatus.SHORTLISTED).count();
        long selected = apps.stream().filter(a -> a.getStatus() == ApplicationStatus.SELECTED).count();
        long inAssessment = apps.stream().filter(a -> a.getStatus() == ApplicationStatus.ASSESSMENT).count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", totalStudents);
        stats.put("totalCompanies", totalCompanies);
        stats.put("activeDrives", activeDrives);
        stats.put("totalApplications", totalApplications);
        stats.put("shortlistedStudents", shortlisted);
        stats.put("selectedStudents", selected);
        stats.put("inAssessment", inAssessment);

        List<Map<String, Object>> driveStats = driveRepository.findAll().stream().map(d -> {
            Map<String, Object> m = new HashMap<>();
            m.put("driveTitle", d.getTitle());
            m.put("companyName", d.getCompany() != null ? d.getCompany().getName() : "Unknown");
            m.put("applicationsCount", d.getApplications() != null ? d.getApplications().size() : 0);
            return m;
        }).collect(Collectors.toList());

        stats.put("driveStats", driveStats);

        return ResponseEntity.ok(stats);
    }

    // --- Student Directory for Admin ---
    @GetMapping("/students")
    public ResponseEntity<List<StudentDTO>> getAllStudents(@RequestParam(required = false) String search) {
        List<Student> students = studentRepository.findAll().stream()
                .filter(s -> s.getRole() == Role.ROLE_STUDENT)
                .filter(s -> search == null || search.isBlank() || 
                        s.getName().toLowerCase().contains(search.toLowerCase()) || 
                        s.getEmail().toLowerCase().contains(search.toLowerCase()) ||
                        (s.getBranch() != null && s.getBranch().toLowerCase().contains(search.toLowerCase())))
                .collect(Collectors.toList());

        List<StudentDTO> dtos = students.stream().map(studentService::mapToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id) {
        Optional<Student> studentOpt = studentRepository.findById(id);
        if (studentOpt.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(studentService.mapToDTO(studentOpt.get()));
    }

    // --- CSV Report Export ---
    @GetMapping("/reports/export")
    public void exportPlacementReport(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"campushire_placement_report.csv\"");

        PrintWriter writer = response.getWriter();
        writer.println("Application ID,Student Name,Student Email,Branch,CGPA,Company Name,Drive Title,Status,Applied Date");

        List<Application> apps = applicationRepository.findAll();
        for (Application a : apps) {
            writer.printf("%d,\"%s\",\"%s\",\"%s\",%.2f,\"%s\",\"%s\",\"%s\",\"%s\"\n",
                    a.getId(),
                    a.getStudent().getName(),
                    a.getStudent().getEmail(),
                    a.getStudent().getBranch() != null ? a.getStudent().getBranch() : "",
                    a.getStudent().getCgpa() != null ? a.getStudent().getCgpa() : 0.0,
                    a.getPlacementDrive().getCompany() != null ? a.getPlacementDrive().getCompany().getName() : "N/A",
                    a.getPlacementDrive().getTitle(),
                    a.getStatus().name(),
                    a.getAppliedDate() != null ? a.getAppliedDate().toString() : ""
            );
        }
        writer.flush();
    }

    // --- Practice Questions Admin Management ---
    @GetMapping("/questions")
    public ResponseEntity<List<PracticeQuestion>> getAllPracticeQuestions() {
        return ResponseEntity.ok(questionRepository.findAll());
    }

    @PostMapping("/questions")
    public ResponseEntity<?> addPracticeQuestion(@RequestBody PracticeQuestion q) {
        PracticeQuestion saved = questionRepository.save(q);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/questions/{id}")
    public ResponseEntity<?> updatePracticeQuestion(@PathVariable Long id, @RequestBody PracticeQuestion q) {
        Optional<PracticeQuestion> opt = questionRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        PracticeQuestion existing = opt.get();
        existing.setTitle(q.getTitle());
        existing.setDescription(q.getDescription());
        existing.setCategory(q.getCategory());
        existing.setDifficulty(q.getDifficulty());
        existing.setTopic(q.getTopic());
        existing.setSampleInput(q.getSampleInput());
        existing.setSampleOutput(q.getSampleOutput());
        existing.setHints(q.getHints());
        existing.setSolutionCode(q.getSolutionCode());
        existing.setExplanation(q.getExplanation());
        existing.setOptionsJson(q.getOptionsJson());
        existing.setCorrectAnswer(q.getCorrectAnswer());
        PracticeQuestion saved = questionRepository.save(existing);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<?> deletePracticeQuestion(@PathVariable Long id) {
        if (!questionRepository.existsById(id)) return ResponseEntity.notFound().build();
        questionRepository.deleteById(id);
        return ResponseEntity.ok(new ApiResponse(true, "Question deleted successfully."));
    }

    // --- Mock Tests Admin Management ---
    @GetMapping("/mock-tests")
    public ResponseEntity<List<MockTest>> getAllMockTests() {
        return ResponseEntity.ok(mockTestRepository.findAll());
    }

    @PostMapping("/mock-tests")
    public ResponseEntity<?> createMockTest(@RequestBody MockTest test) {
        MockTest saved = mockTestRepository.save(test);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/mock-tests/{id}")
    public ResponseEntity<?> updateMockTest(@PathVariable Long id, @RequestBody MockTest test) {
        Optional<MockTest> opt = mockTestRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        MockTest existing = opt.get();
        existing.setTitle(test.getTitle());
        existing.setCategory(test.getCategory());
        existing.setDurationMinutes(test.getDurationMinutes());
        existing.setTotalMarks(test.getTotalMarks());
        existing.setPassPercentage(test.getPassPercentage());
        existing.setDescription(test.getDescription());
        existing.setQuestionsJson(test.getQuestionsJson());
        MockTest saved = mockTestRepository.save(existing);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/mock-tests/{id}")
    public ResponseEntity<?> deleteMockTest(@PathVariable Long id) {
        if (!mockTestRepository.existsById(id)) return ResponseEntity.notFound().build();
        mockTestRepository.deleteById(id);
        return ResponseEntity.ok(new ApiResponse(true, "Mock test deleted successfully."));
    }

    // --- Broadcast Notifications ---
    @PostMapping("/notifications/broadcast")
    public ResponseEntity<?> broadcastNotification(@RequestBody Map<String, String> payload) {
        String title = payload.getOrDefault("title", "Campus Placement Announcement");
        String message = payload.getOrDefault("message", "");
        String linkUrl = payload.getOrDefault("linkUrl", "/student/dashboard");
        String type = payload.getOrDefault("type", "ANNOUNCEMENT");

        List<Student> students = studentRepository.findAll().stream()
                .filter(s -> s.getRole() == Role.ROLE_STUDENT)
                .collect(Collectors.toList());

        for (Student s : students) {
            Notification n = new Notification();
            n.setStudent(s);
            n.setTitle(title);
            n.setMessage(message);
            n.setLinkUrl(linkUrl);
            n.setType(type);
            notificationRepository.save(n);
        }

        return ResponseEntity.ok(Map.of("message", "Broadcast sent to " + students.size() + " students."));
    }

    // --- Companies Management ---
    @GetMapping("/companies")
    public ResponseEntity<List<CompanyDTO>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @PostMapping("/companies")
    public ResponseEntity<CompanyDTO> createCompany(@Valid @RequestBody CompanyDTO dto) {
        return ResponseEntity.ok(companyService.createCompany(dto));
    }

    @PutMapping("/companies/{id}")
    public ResponseEntity<CompanyDTO> updateCompany(@PathVariable Long id, @Valid @RequestBody CompanyDTO dto) {
        return ResponseEntity.ok(companyService.updateCompany(id, dto));
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<ApiResponse> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.ok(new ApiResponse(true, "Company deleted successfully."));
    }

    // --- Placement Drives Management ---
    @GetMapping("/drives")
    public ResponseEntity<List<PlacementDriveDTO>> getAllDrives() {
        return ResponseEntity.ok(driveService.getAllDrives());
    }

    @PostMapping("/drives")
    public ResponseEntity<PlacementDriveDTO> createDrive(@Valid @RequestBody PlacementDriveDTO dto) {
        return ResponseEntity.ok(driveService.createDrive(dto));
    }

    @PutMapping("/drives/{id}")
    public ResponseEntity<PlacementDriveDTO> updateDrive(@PathVariable Long id, @Valid @RequestBody PlacementDriveDTO dto) {
        return ResponseEntity.ok(driveService.updateDrive(id, dto));
    }

    @PutMapping("/drives/{id}/toggle-status")
    public ResponseEntity<PlacementDriveDTO> toggleDriveStatus(@PathVariable Long id) {
        return ResponseEntity.ok(driveService.toggleDriveStatus(id));
    }

    @DeleteMapping("/drives/{id}")
    public ResponseEntity<ApiResponse> deleteDrive(@PathVariable Long id) {
        driveService.deleteDrive(id);
        return ResponseEntity.ok(new ApiResponse(true, "Placement drive deleted successfully."));
    }

    // --- Applications & Recruitment Status ---
    @GetMapping("/applications")
    public ResponseEntity<List<ApplicationDTO>> getAllApplications() {
        return ResponseEntity.ok(applicationService.getAllApplications());
    }

    @PutMapping("/applications/{id}/status")
    public ResponseEntity<ApplicationDTO> updateApplicationStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(applicationService.updateStatus(id, request.getStatus()));
    }
}

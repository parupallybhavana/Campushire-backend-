package com.example.campushire.controller;

import com.example.campushire.entity.Resume;
import com.example.campushire.entity.Student;
import com.example.campushire.repository.ResumeRepository;
import com.example.campushire.repository.StudentRepository;
import com.example.campushire.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/resumes")
@CrossOrigin(origins = "*")
public class ResumeController {

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private Student getAuthenticatedStudent(String authHeader) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null && !"anonymousUser".equals(auth.getName())) {
            Optional<Student> opt = studentRepository.findByEmail(auth.getName());
            if (opt.isPresent()) return opt.get();
        }
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String email = jwtUtil.extractUsername(token);
            return studentRepository.findByEmail(email).orElse(null);
        }
        return null;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Student student = getAuthenticatedStudent(authHeader);
        if (student == null) return ResponseEntity.status(401).body("Unauthorized");

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Please select a file to upload."));
        }

        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.toLowerCase().endsWith(".pdf") && !filename.toLowerCase().endsWith(".docx"))) {
            return ResponseEntity.badRequest().body(Map.of("message", "Only PDF or DOCX files are allowed."));
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            return ResponseEntity.badRequest().body(Map.of("message", "File size must be under 5MB."));
        }

        try {
            byte[] bytes = file.getBytes();
            String base64 = Base64.getEncoder().encodeToString(bytes);

            // Simulates text parsing & skill extraction from resume content
            String extractedSkills = "Java, React, SQL, Spring Boot, Data Structures, HTML, CSS, Git, REST APIs";

            Resume resume = new Resume();
            resume.setStudent(student);
            resume.setFilename(filename);
            resume.setFileType(file.getContentType());
            resume.setFileSize(file.getSize());
            resume.setFileContentBase64(base64);
            resume.setExtractedSkills(extractedSkills);
            resume.setParsedText("Resume extracted text content for " + student.getName() + ": Computer Science undergrad skilled in Java, Spring Boot, React, and Database design.");
            resume.setUploadedAt(LocalDateTime.now());

            resumeRepository.save(resume);

            Map<String, Object> resp = new HashMap<>();
            resp.put("id", resume.getId());
            resp.put("filename", resume.getFilename());
            resp.put("uploadedAt", resume.getUploadedAt());
            resp.put("extractedSkills", extractedSkills.split(", "));
            resp.put("completenessScore", 85);
            resp.put("message", "Resume uploaded & analyzed successfully!");

            return ResponseEntity.ok(resp);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to read uploaded file."));
        }
    }

    @GetMapping("/latest")
    public ResponseEntity<?> getLatestResume(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Student student = getAuthenticatedStudent(authHeader);
        if (student == null) return ResponseEntity.status(401).body("Unauthorized");

        Optional<Resume> resumeOpt = resumeRepository.findFirstByStudentIdOrderByUploadedAtDesc(student.getId());
        if (resumeOpt.isEmpty()) {
            return ResponseEntity.ok(Map.of("hasResume", false));
        }

        Resume resume = resumeOpt.get();
        Map<String, Object> resp = new HashMap<>();
        resp.put("hasResume", true);
        resp.put("id", resume.getId());
        resp.put("filename", resume.getFilename());
        resp.put("fileSize", resume.getFileSize());
        resp.put("uploadedAt", resume.getUploadedAt());
        resp.put("extractedSkills", resume.getExtractedSkills() != null ? resume.getExtractedSkills().split(", ") : new String[0]);
        resp.put("completenessScore", 85);

        List<String> checklist = Arrays.asList(
                "Contact Info & Education details documented",
                "Technical Skills Section clearly formatted",
                "Project descriptions documented",
                "Academic details & CGPA verified"
        );
        resp.put("checklist", checklist);

        return ResponseEntity.ok(resp);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadResumeById(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Student student = getAuthenticatedStudent(authHeader);
        if (student == null) return ResponseEntity.status(401).body("Unauthorized");

        Optional<Resume> resumeOpt = resumeRepository.findById(id);
        if (resumeOpt.isEmpty()) return ResponseEntity.notFound().build();

        Resume resume = resumeOpt.get();
        if (!resume.getStudent().getId().equals(student.getId()) && student.getRole() != com.example.campushire.entity.Role.ROLE_ADMIN) {
            return ResponseEntity.status(403).body("Access denied.");
        }

        if (resume.getFileContentBase64() == null) {
            return ResponseEntity.badRequest().body("Resume file content is not available.");
        }

        byte[] fileBytes = Base64.getDecoder().decode(resume.getFileContentBase64());
        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resume.getFilename() + "\"")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(fileBytes);
    }

    @GetMapping("/latest/download")
    public ResponseEntity<?> downloadLatestResume(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Student student = getAuthenticatedStudent(authHeader);
        if (student == null) return ResponseEntity.status(401).body("Unauthorized");

        Optional<Resume> resumeOpt = resumeRepository.findFirstByStudentIdOrderByUploadedAtDesc(student.getId());
        if (resumeOpt.isEmpty()) return ResponseEntity.notFound().build();

        Resume resume = resumeOpt.get();
        if (resume.getFileContentBase64() == null) {
            return ResponseEntity.badRequest().body("Resume file content is not available.");
        }

        byte[] fileBytes = Base64.getDecoder().decode(resume.getFileContentBase64());
        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resume.getFilename() + "\"")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(fileBytes);
    }

    @DeleteMapping("/latest")
    public ResponseEntity<?> deleteLatestResume(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Student student = getAuthenticatedStudent(authHeader);
        if (student == null) return ResponseEntity.status(401).body("Unauthorized");

        List<Resume> resumes = resumeRepository.findByStudentId(student.getId());
        if (resumes.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "No resume found to delete."));
        }

        resumeRepository.deleteAll(resumes);
        return ResponseEntity.ok(Map.of("message", "Resume deleted successfully."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteResumeById(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Student student = getAuthenticatedStudent(authHeader);
        if (student == null) return ResponseEntity.status(401).body("Unauthorized");

        Optional<Resume> resumeOpt = resumeRepository.findById(id);
        if (resumeOpt.isPresent()) {
            Resume resume = resumeOpt.get();
            if (resume.getStudent().getId().equals(student.getId()) || student.getRole() == com.example.campushire.entity.Role.ROLE_ADMIN) {
                resumeRepository.delete(resume);
                return ResponseEntity.ok(Map.of("message", "Resume deleted successfully."));
            }
        }
        return ResponseEntity.notFound().build();
    }
}

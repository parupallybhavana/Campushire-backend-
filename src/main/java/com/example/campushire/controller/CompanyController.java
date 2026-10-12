package com.example.campushire.controller;

import com.example.campushire.entity.*;
import com.example.campushire.repository.*;
import com.example.campushire.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/companies")
@CrossOrigin(origins = "*")
public class CompanyController {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private SavedCompanyRepository savedCompanyRepository;

    @Autowired
    private InterviewExperienceRepository experienceRepository;

    @Autowired
    private CompanyReviewRepository reviewRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private Student getAuthenticatedStudent(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String email = jwtUtil.extractUsername(token);
            return studentRepository.findByEmail(email).orElse(null);
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllCompanies(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String location,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Student student = getAuthenticatedStudent(authHeader);
        List<Company> companies = companyRepository.findAll();

        List<Map<String, Object>> response = companies.stream()
                .filter(c -> search == null || c.getName().toLowerCase().contains(search.toLowerCase()) || (c.getIndustry() != null && c.getIndustry().toLowerCase().contains(search.toLowerCase())))
                .filter(c -> industry == null || industry.isEmpty() || (c.getIndustry() != null && c.getIndustry().equalsIgnoreCase(industry)))
                .filter(c -> location == null || location.isEmpty() || (c.getLocation() != null && c.getLocation().toLowerCase().contains(location.toLowerCase())))
                .map(c -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", c.getId());
                    map.put("name", c.getName());
                    map.put("description", c.getDescription());
                    map.put("location", c.getLocation());
                    map.put("website", c.getWebsite());
                    map.put("logoUrl", c.getLogoUrl());
                    map.put("industry", c.getIndustry());
                    map.put("ctcPackage", c.getCtcPackage());
                    map.put("companySize", c.getCompanySize());
                    map.put("headquarters", c.getHeadquarters());
                    map.put("driveCount", c.getDrives() != null ? c.getDrives().size() : 0);
                    map.put("isSaved", student != null && savedCompanyRepository.existsByStudentIdAndCompanyId(student.getId(), c.getId()));
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCompanyById(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Optional<Company> companyOpt = companyRepository.findById(id);
        if (companyOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Company company = companyOpt.get();
        Student student = getAuthenticatedStudent(authHeader);

        Map<String, Object> map = new HashMap<>();
        map.put("id", company.getId());
        map.put("name", company.getName());
        map.put("description", company.getDescription());
        map.put("location", company.getLocation());
        map.put("website", company.getWebsite());
        map.put("logoUrl", company.getLogoUrl());
        map.put("industry", company.getIndustry());
        map.put("headquarters", company.getHeadquarters());
        map.put("companySize", company.getCompanySize());
        map.put("ctcPackage", company.getCtcPackage());
        map.put("selectionProcess", company.getSelectionProcess());
        map.put("techTopics", company.getTechTopics());
        map.put("aptitudeTopics", company.getAptitudeTopics());
        map.put("hrQuestions", company.getHrQuestions());
        map.put("prepChecklist", company.getPrepChecklist());
        map.put("isSaved", student != null && savedCompanyRepository.existsByStudentIdAndCompanyId(student.getId(), company.getId()));

        List<Map<String, Object>> drivesList = company.getDrives().stream().map(d -> {
            Map<String, Object> dMap = new HashMap<>();
            dMap.put("id", d.getId());
            dMap.put("title", d.getTitle());
            dMap.put("description", d.getDescription());
            dMap.put("minCgpa", d.getMinCgpa());
            dMap.put("requiredSkills", d.getRequiredSkills());
            dMap.put("location", d.getLocation());
            dMap.put("salaryCtc", d.getSalaryCtc());
            dMap.put("eligibleBranches", d.getEligibleBranches());
            dMap.put("jobRole", d.getJobRole());
            dMap.put("deadline", d.getDeadline());
            dMap.put("recruitmentStages", d.getRecruitmentStages());
            return dMap;
        }).collect(Collectors.toList());

        map.put("drives", drivesList);

        return ResponseEntity.ok(map);
    }

    @GetMapping("/{id}/readiness")
    public ResponseEntity<?> calculateReadiness(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = true) String authHeader) {

        Student student = getAuthenticatedStudent(authHeader);
        if (student == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Optional<Company> companyOpt = companyRepository.findById(id);
        if (companyOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Company company = companyOpt.get();

        // Calculate score based on actual student skills & practice data
        String studentSkills = student.getSkills() != null ? student.getSkills().toLowerCase() : "";
        String techTopics = company.getTechTopics() != null ? company.getTechTopics().toLowerCase() : "";

        int skillMatchCount = 0;
        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        if (!techTopics.isEmpty()) {
            String[] required = techTopics.split("[,;\\|]");
            for (String req : required) {
                String cleanReq = req.trim();
                if (!cleanReq.isEmpty()) {
                    if (studentSkills.contains(cleanReq.toLowerCase())) {
                        skillMatchCount++;
                        matchedSkills.add(cleanReq);
                    } else {
                        missingSkills.add(cleanReq);
                    }
                }
            }
        }

        int baseSkillScore = matchedSkills.size() > 0 ? Math.min(95, 50 + (skillMatchCount * 15)) : 45;
        int codingBonus = Math.min(25, student.getSolvedCodingCount() * 2);
        int aptitudeBonus = Math.min(20, student.getSolvedAptitudeCount());
        int mockBonus = Math.min(15, student.getCompletedMockCount() * 5);

        int overallScore = Math.min(98, (int) (baseSkillScore * 0.4 + codingBonus + aptitudeBonus + mockBonus));

        Map<String, Object> result = new HashMap<>();
        result.put("companyId", company.getId());
        result.put("companyName", company.getName());
        result.put("overallScore", overallScore);
        result.put("skillsMatchScore", baseSkillScore);
        result.put("codingScore", Math.min(100, 40 + codingBonus * 2));
        result.put("aptitudeScore", Math.min(100, 50 + aptitudeBonus * 2));
        result.put("technicalScore", baseSkillScore);
        result.put("communicationScore", Math.min(100, 60 + mockBonus * 2));
        result.put("matchedSkills", matchedSkills);
        result.put("missingSkills", missingSkills);
        result.put("strongAreas", Arrays.asList("Core Computer Science Concepts", "Aptitude Fundamentals"));
        result.put("areasToImprove", missingSkills.size() > 0 ? missingSkills : Arrays.asList("System Design", "Mock Interview Practice"));

        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/save")
    public ResponseEntity<?> toggleSaveCompany(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = true) String authHeader) {

        Student student = getAuthenticatedStudent(authHeader);
        if (student == null) return ResponseEntity.status(401).body("Unauthorized");

        Optional<Company> companyOpt = companyRepository.findById(id);
        if (companyOpt.isEmpty()) return ResponseEntity.notFound().build();

        Company company = companyOpt.get();
        boolean exists = savedCompanyRepository.existsByStudentIdAndCompanyId(student.getId(), company.getId());

        if (exists) {
            savedCompanyRepository.deleteByStudentIdAndCompanyId(student.getId(), company.getId());
            return ResponseEntity.ok(Map.of("saved", false, "message", "Company removed from saved list."));
        } else {
            savedCompanyRepository.save(new SavedCompany(student, company));
            return ResponseEntity.ok(Map.of("saved", true, "message", "Company saved successfully!"));
        }
    }

    @GetMapping("/saved")
    public ResponseEntity<?> getSavedCompanies(@RequestHeader(value = "Authorization", required = true) String authHeader) {
        Student student = getAuthenticatedStudent(authHeader);
        if (student == null) return ResponseEntity.status(401).body("Unauthorized");

        List<SavedCompany> savedList = savedCompanyRepository.findByStudentId(student.getId());
        List<Map<String, Object>> result = savedList.stream().map(s -> {
            Company c = s.getCompany();
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("name", c.getName());
            map.put("logoUrl", c.getLogoUrl());
            map.put("industry", c.getIndustry());
            map.put("location", c.getLocation());
            map.put("ctcPackage", c.getCtcPackage());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/experiences")
    public ResponseEntity<?> getExperiences(@PathVariable Long id) {
        List<InterviewExperience> list = experienceRepository.findByCompanyIdAndStatus(id, "APPROVED");
        List<Map<String, Object>> res = list.stream().map(e -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", e.getId());
            m.put("studentName", e.getStudent() != null ? e.getStudent().getName() : "Anonymous");
            m.put("title", e.getTitle());
            m.put("roleTitle", e.getRoleTitle());
            m.put("difficulty", e.getDifficulty());
            m.put("roundDetails", e.getRoundDetails());
            m.put("questionsAsked", e.getQuestionsAsked());
            m.put("preparationTips", e.getPreparationTips());
            m.put("createdAt", e.getCreatedAt());
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(res);
    }

    @PostMapping("/{id}/experiences")
    public ResponseEntity<?> addExperience(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload,
            @RequestHeader(value = "Authorization", required = true) String authHeader) {

        Student student = getAuthenticatedStudent(authHeader);
        if (student == null) return ResponseEntity.status(401).body("Unauthorized");

        Optional<Company> companyOpt = companyRepository.findById(id);
        if (companyOpt.isEmpty()) return ResponseEntity.notFound().build();

        InterviewExperience exp = new InterviewExperience(
                null,
                student,
                companyOpt.get(),
                payload.getOrDefault("title", "Interview Experience"),
                payload.getOrDefault("roleTitle", "Software Engineer"),
                payload.getOrDefault("difficulty", "MEDIUM"),
                payload.get("roundDetails"),
                payload.get("questionsAsked"),
                payload.get("preparationTips")
        );

        experienceRepository.save(exp);
        return ResponseEntity.ok(Map.of("message", "Interview experience submitted successfully!"));
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<?> getReviews(@PathVariable Long id) {
        List<CompanyReview> list = reviewRepository.findByCompanyIdAndStatus(id, "APPROVED");
        List<Map<String, Object>> res = list.stream().map(r -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", r.getId());
            m.put("studentName", r.getStudent() != null ? r.getStudent().getName() : "Anonymous");
            m.put("rating", r.getRating());
            m.put("title", r.getTitle());
            m.put("reviewText", r.getReviewText());
            m.put("createdAt", r.getCreatedAt());
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(res);
    }

    @PostMapping("/{id}/reviews")
    public ResponseEntity<?> addReview(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "Authorization", required = true) String authHeader) {

        Student student = getAuthenticatedStudent(authHeader);
        if (student == null) return ResponseEntity.status(401).body("Unauthorized");

        Optional<Company> companyOpt = companyRepository.findById(id);
        if (companyOpt.isEmpty()) return ResponseEntity.notFound().build();

        Integer rating = payload.get("rating") != null ? Integer.parseInt(payload.get("rating").toString()) : 5;

        CompanyReview review = new CompanyReview(
                null,
                student,
                companyOpt.get(),
                rating,
                (String) payload.getOrDefault("title", "Student Review"),
                (String) payload.get("reviewText")
        );

        reviewRepository.save(review);
        return ResponseEntity.ok(Map.of("message", "Company review submitted successfully!"));
    }
}

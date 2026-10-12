package com.example.campushire.controller;

import com.example.campushire.entity.MockInterviewSession;
import com.example.campushire.entity.Student;
import com.example.campushire.repository.MockInterviewSessionRepository;
import com.example.campushire.repository.StudentRepository;
import com.example.campushire.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mock-interviews")
@CrossOrigin(origins = "*")
public class MockInterviewController {

    @Autowired
    private MockInterviewSessionRepository sessionRepository;

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

    @GetMapping({"", "/", "/sessions"})
    public ResponseEntity<?> getSessions(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Student student = getAuthenticatedStudent(authHeader);
        if (student == null) return ResponseEntity.status(401).body("Unauthorized");

        List<MockInterviewSession> list = sessionRepository.findByStudentId(student.getId());
        List<Map<String, Object>> result = list.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", s.getId());
            map.put("interviewType", s.getInterviewType());
            map.put("targetRole", s.getTargetRole());
            map.put("difficulty", s.getDifficulty());
            map.put("scheduledTime", s.getScheduledTime());
            map.put("durationMinutes", s.getDurationMinutes());
            map.put("status", s.getStatus());
            map.put("score", s.getScore());
            map.put("feedbackText", s.getFeedbackText());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @PostMapping("/schedule")
    public ResponseEntity<?> scheduleSession(
            @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Student student = getAuthenticatedStudent(authHeader);
        if (student == null) return ResponseEntity.status(401).body("Unauthorized");

        MockInterviewSession session = new MockInterviewSession();
        session.setStudent(student);
        session.setInterviewType((String) payload.getOrDefault("interviewType", "TECHNICAL"));
        session.setTargetRole((String) payload.getOrDefault("targetRole", "Software Engineer"));
        session.setDifficulty((String) payload.getOrDefault("difficulty", "INTERMEDIATE"));
        session.setScheduledTime(LocalDateTime.now().plusDays(1));
        session.setDurationMinutes(payload.get("durationMinutes") != null ? Integer.parseInt(payload.get("durationMinutes").toString()) : 30);
        session.setStatus("SCHEDULED");

        sessionRepository.save(session);

        return ResponseEntity.ok(Map.of("message", "Mock interview scheduled successfully!", "sessionId", session.getId()));
    }

    @GetMapping("/{id}/start")
    public ResponseEntity<?> startSession(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Student student = getAuthenticatedStudent(authHeader);
        if (student == null) return ResponseEntity.status(401).body("Unauthorized");

        Optional<MockInterviewSession> sOpt = sessionRepository.findById(id);
        if (sOpt.isEmpty()) return ResponseEntity.notFound().build();

        MockInterviewSession session = sOpt.get();

        // Sample question flow for solo practice room
        List<Map<String, String>> questions = Arrays.asList(
                Map.of("questionNumber", "1", "question", "Tell me about yourself and your technical interest in " + session.getTargetRole() + "."),
                Map.of("questionNumber", "2", "question", "Explain your most challenging project and how you handled technical obstacles."),
                Map.of("questionNumber", "3", "question", "What is the difference between REST and GraphQL, or SQL vs NoSQL?"),
                Map.of("questionNumber", "4", "question", "Why do you want to join this organization?")
        );

        Map<String, Object> resp = new HashMap<>();
        resp.put("sessionId", session.getId());
        resp.put("targetRole", session.getTargetRole());
        resp.put("interviewType", session.getInterviewType());
        resp.put("questions", questions);

        return ResponseEntity.ok(resp);
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<?> completeSession(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Student student = getAuthenticatedStudent(authHeader);
        if (student == null) return ResponseEntity.status(401).body("Unauthorized");

        Optional<MockInterviewSession> sOpt = sessionRepository.findById(id);
        if (sOpt.isEmpty()) return ResponseEntity.notFound().build();

        MockInterviewSession session = sOpt.get();
        session.setStatus("COMPLETED");
        session.setScore(88);
        session.setFeedbackText("Great structured responses using STAR method. Technical concepts were clear and confident.");

        sessionRepository.save(session);

        student.setCompletedMockCount(student.getCompletedMockCount() + 1);
        student.setTotalXp(student.getTotalXp() + 40);
        studentRepository.save(student);

        return ResponseEntity.ok(Map.of("message", "Mock interview completed!", "score", 88, "xpEarned", 40));
    }
}

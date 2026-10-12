package com.example.campushire.controller;

import com.example.campushire.entity.Student;
import com.example.campushire.repository.StudentRepository;
import com.example.campushire.security.JwtUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AiAssistantController {

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

    @PostMapping("/chat")
    public ResponseEntity<?> chatWithAi(
            @RequestBody Map<String, String> payload,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String userPrompt = payload.get("prompt");
        if (userPrompt == null || userPrompt.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("reply", "Please ask a valid placement or interview preparation question."));
        }

        Student student = getAuthenticatedStudent(authHeader);
        String studentName = student != null ? student.getName() : "Student";
        String lowerPrompt = userPrompt.toLowerCase();

        String responseText = "";

        if (lowerPrompt.contains("dsa") || lowerPrompt.contains("data structure") || lowerPrompt.contains("java")) {
            responseText = "Hello " + studentName + "! For Java & DSA placement preparation, here is your 4-step action plan:\n\n" +
                    "1. **Arrays & Strings**: Practice 2-pointer approach and sliding window (e.g., Two Sum, Max Subarray).\n" +
                    "2. **Core Java & Spring**: Focus on Object-Oriented Principles, HashMap internal working, @Autowired, and REST annotations.\n" +
                    "3. **Trees & Graphs**: Master BFS/DFS traversals and Binary Search Tree properties.\n" +
                    "4. **Timed Drills**: Take a 30-minute timed mock test in the CampusHire Practice Zone to build speed.\n\n" +
                    "Would you like me to generate a 7-day custom study planner for Java Developer roles?";
        } else if (lowerPrompt.contains("resume") || lowerPrompt.contains("cv") || lowerPrompt.contains("project")) {
            responseText = "Hello " + studentName + "! Here are key tips to optimize your resume for campus placement drives:\n\n" +
                    "• **Action Verbs**: Start bullet points with impact words like *Architected*, *Implemented*, *Optimized*, or *Designed*.\n" +
                    "• **Quantify Results**: Add numbers (e.g., 'Improved API response speed by 35%' or 'Handled 500+ daily user requests').\n" +
                    "• **Match Keywords**: Ensure skills mentioned in company job descriptions (e.g., React, SQL, Spring Boot) are listed clearly.\n" +
                    "• **CampusHire Resume Tool**: Upload your resume PDF in our **Resume Analyzer** tab to run instant ATS keyword comparison!";
        } else if (lowerPrompt.contains("interview") || lowerPrompt.contains("hr") || lowerPrompt.contains("tell me about yourself")) {
            responseText = "For HR & Behavioral rounds, use the **STAR Method** (Situation, Task, Action, Result):\n\n" +
                    "1. **Situation**: Briefly describe the context or challenge.\n" +
                    "2. **Task**: Explain what your exact responsibility was.\n" +
                    "3. **Action**: Highlight the specific steps and tools YOU used to solve it.\n" +
                    "4. **Result**: End with positive quantifiable outcome and key learnings.\n\n" +
                    "You can practice solo HR mock sessions in the **Mock Interview Module** right now!";
        } else {
            responseText = "Welcome " + studentName + "! As your CampusHire AI Placement Assistant, I can guide you on:\n\n" +
                    "• Personalized placement study plans & weekly targets\n" +
                    "• Skill gap analysis for specific companies (Google, Microsoft, AWS)\n" +
                    "• Technical interview question practice & DSA strategy\n" +
                    "• Resume feedback & project description optimization\n\n" +
                    "Feel free to ask any specific question about your career journey!";
        }

        Map<String, Object> reply = new HashMap<>();
        reply.put("reply", responseText);
        reply.put("timestamp", new Date());
        reply.put("suggestedActions", Arrays.asList("Start Coding Practice", "Check Skill Gap", "Upload Resume"));

        return ResponseEntity.ok(reply);
    }
}

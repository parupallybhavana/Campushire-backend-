package com.example.campushire.controller;

import com.example.campushire.entity.Notification;
import com.example.campushire.entity.Student;
import com.example.campushire.repository.NotificationRepository;
import com.example.campushire.repository.StudentRepository;
import com.example.campushire.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

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
    public ResponseEntity<?> getNotifications(@RequestHeader(value = "Authorization", required = true) String authHeader) {
        Student student = getAuthenticatedStudent(authHeader);
        if (student == null) return ResponseEntity.status(401).body("Unauthorized");

        List<Notification> list = notificationRepository.findByStudentIdOrderByCreatedAtDesc(student.getId());
        long unreadCount = notificationRepository.countByStudentIdAndIsReadFalse(student.getId());

        List<Map<String, Object>> result = list.stream().map(n -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", n.getId());
            m.put("title", n.getTitle());
            m.put("message", n.getMessage());
            m.put("linkUrl", n.getLinkUrl());
            m.put("isRead", n.getIsRead());
            m.put("type", n.getType());
            m.put("createdAt", n.getCreatedAt());
            return m;
        }).collect(Collectors.toList());

        Map<String, Object> resp = new HashMap<>();
        resp.put("unreadCount", unreadCount);
        resp.put("notifications", result);

        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = true) String authHeader) {

        Student student = getAuthenticatedStudent(authHeader);
        if (student == null) return ResponseEntity.status(401).body("Unauthorized");

        Optional<Notification> nOpt = notificationRepository.findById(id);
        if (nOpt.isPresent() && nOpt.get().getStudent().getId().equals(student.getId())) {
            Notification n = nOpt.get();
            n.setIsRead(true);
            notificationRepository.save(n);
        }

        return ResponseEntity.ok(Map.of("message", "Notification marked as read."));
    }

    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(@RequestHeader(value = "Authorization", required = true) String authHeader) {
        Student student = getAuthenticatedStudent(authHeader);
        if (student == null) return ResponseEntity.status(401).body("Unauthorized");

        List<Notification> list = notificationRepository.findByStudentIdOrderByCreatedAtDesc(student.getId());
        for (Notification n : list) {
            n.setIsRead(true);
        }
        notificationRepository.saveAll(list);

        return ResponseEntity.ok(Map.of("message", "All notifications marked as read."));
    }
}

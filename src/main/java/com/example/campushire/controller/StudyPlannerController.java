package com.example.campushire.controller;

import com.example.campushire.entity.Student;
import com.example.campushire.entity.StudyPlanTask;
import com.example.campushire.repository.StudentRepository;
import com.example.campushire.repository.StudyPlanTaskRepository;
import com.example.campushire.security.JwtUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/study-plan")
@CrossOrigin(origins = "*")
public class StudyPlannerController {

    @Autowired
    private StudyPlanTaskRepository taskRepository;

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

    @GetMapping
    public ResponseEntity<?> getTasks(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Student student = getAuthenticatedStudent(authHeader);
        if (student == null) return ResponseEntity.status(401).body("Unauthorized");

        List<StudyPlanTask> tasks = taskRepository.findByStudentId(student.getId());

        long completedCount = tasks.stream().filter(t -> Boolean.TRUE.equals(t.getCompleted())).count();
        int completionPercentage = tasks.size() > 0 ? (int) Math.round((double) completedCount / tasks.size() * 100.0) : 0;

        Map<String, Object> resp = new HashMap<>();
        resp.put("completionPercentage", completionPercentage);
        resp.put("totalTasks", tasks.size());
        resp.put("completedTasks", completedCount);

        List<Map<String, Object>> taskList = tasks.stream().map(t -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", t.getId());
            m.put("title", t.getTitle());
            m.put("category", t.getCategory());
            m.put("dayOfWeek", t.getDayOfWeek());
            m.put("scheduledDate", t.getScheduledDate());
            m.put("completed", t.getCompleted());
            m.put("notes", t.getNotes());
            return m;
        }).collect(Collectors.toList());

        resp.put("tasks", taskList);

        return ResponseEntity.ok(resp);
    }

    @PostMapping
    public ResponseEntity<?> addTask(
            @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Student student = getAuthenticatedStudent(authHeader);
        if (student == null) return ResponseEntity.status(401).body("Unauthorized");

        StudyPlanTask task = new StudyPlanTask();
        task.setStudent(student);
        task.setTitle((String) payload.getOrDefault("title", "Practice Task"));
        task.setCategory((String) payload.getOrDefault("category", "CODING"));
        task.setDayOfWeek((String) payload.getOrDefault("dayOfWeek", "MONDAY"));
        task.setScheduledDate(LocalDate.now());
        task.setCompleted(false);
        task.setNotes((String) payload.get("notes"));

        taskRepository.save(task);

        return ResponseEntity.ok(Map.of("message", "Task added successfully!", "taskId", task.getId()));
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<?> toggleTask(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Student student = getAuthenticatedStudent(authHeader);
        if (student == null) return ResponseEntity.status(401).body("Unauthorized");

        Optional<StudyPlanTask> tOpt = taskRepository.findById(id);
        if (tOpt.isEmpty()) return ResponseEntity.notFound().build();

        StudyPlanTask task = tOpt.get();
        if (!task.getStudent().getId().equals(student.getId())) {
            return ResponseEntity.status(403).body("Access denied.");
        }

        task.setCompleted(!task.getCompleted());
        taskRepository.save(task);

        if (task.getCompleted()) {
            student.setTotalXp(student.getTotalXp() + 15);
            studentRepository.save(student);
        }

        return ResponseEntity.ok(Map.of("completed", task.getCompleted(), "xpEarned", task.getCompleted() ? 15 : 0));
    }
}

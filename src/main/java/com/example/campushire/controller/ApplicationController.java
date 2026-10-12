package com.example.campushire.controller;

import com.example.campushire.dto.ApplicationDTO;
import com.example.campushire.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @PostMapping("/apply/{jobId}")
    public ResponseEntity<ApplicationDTO> applyForJob(Authentication authentication, @PathVariable Long jobId) {
        String email = authentication.getName();
        return ResponseEntity.ok(applicationService.applyForJob(email, jobId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ApplicationDTO>> getMyApplications(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(applicationService.getMyApplications(email));
    }
}

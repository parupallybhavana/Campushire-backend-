package com.example.campushire.controller;

import com.example.campushire.dto.PlacementDriveDTO;
import com.example.campushire.service.PlacementDriveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private PlacementDriveService driveService;

    @GetMapping
    public ResponseEntity<List<PlacementDriveDTO>> getAllJobs() {
        return ResponseEntity.ok(driveService.getAllDrives());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlacementDriveDTO> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(driveService.getDriveById(id));
    }
}

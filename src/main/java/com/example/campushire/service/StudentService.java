package com.example.campushire.service;

import com.example.campushire.dto.StudentDTO;
import com.example.campushire.entity.Student;
import com.example.campushire.exception.BadRequestException;
import com.example.campushire.exception.ResourceNotFoundException;
import com.example.campushire.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public StudentDTO getProfile(String email) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for: " + email));
        return mapToDTO(student);
    }

    @Transactional
    public StudentDTO updateProfile(String email, StudentDTO dto) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (dto.getCgpa() != null) {
            if (dto.getCgpa() < 0.0 || dto.getCgpa() > 10.0) {
                throw new BadRequestException("CGPA must be between 0 and 10.");
            }
            student.setCgpa(dto.getCgpa());
        }

        if (dto.getName() != null && !dto.getName().isBlank()) {
            student.setName(dto.getName());
        }

        if (dto.getBranch() != null) {
            student.setBranch(dto.getBranch());
        }

        if (dto.getGraduationYear() != null) {
            student.setGraduationYear(dto.getGraduationYear());
        }

        if (dto.getSkills() != null) {
            student.setSkills(dto.getSkills());
        }

        if (dto.getDegree() != null) {
            student.setDegree(dto.getDegree());
        }

        if (dto.getPhone() != null) {
            student.setPhone(dto.getPhone());
        }

        if (dto.getBacklogs() != null) {
            student.setBacklogs(dto.getBacklogs());
        }

        if (dto.getProjects() != null) {
            student.setProjects(dto.getProjects());
        }

        if (dto.getCertifications() != null) {
            student.setCertifications(dto.getCertifications());
        }

        Student updated = studentRepository.save(student);
        return mapToDTO(updated);
    }

    public StudentDTO mapToDTO(Student student) {
        StudentDTO dto = new StudentDTO(
                student.getId(),
                student.getName(),
                student.getEmail(),
                student.getBranch(),
                student.getCgpa(),
                student.getGraduationYear(),
                student.getSkills(),
                student.getRole().name()
        );
        dto.setPhone(student.getPhone());
        dto.setDegree(student.getDegree());
        dto.setBacklogs(student.getBacklogs());
        dto.setProjects(student.getProjects());
        dto.setCertifications(student.getCertifications());
        dto.setStreakDays(student.getStreakDays());
        dto.setTotalXp(student.getTotalXp());
        return dto;
    }
}

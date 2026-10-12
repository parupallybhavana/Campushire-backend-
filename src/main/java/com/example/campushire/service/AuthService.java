package com.example.campushire.service;

import com.example.campushire.dto.AuthRequest;
import com.example.campushire.dto.AuthResponse;
import com.example.campushire.dto.RegisterRequest;
import com.example.campushire.dto.StudentDTO;
import com.example.campushire.entity.Role;
import com.example.campushire.entity.Student;
import com.example.campushire.exception.BadRequestException;
import com.example.campushire.repository.StudentRepository;
import com.example.campushire.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    public AuthResponse register(RegisterRequest request) {
        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email address is already registered.");
        }

        Student student = new Student();
        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setPassword(passwordEncoder.encode(request.getPassword()));
        student.setBranch(request.getBranch());
        student.setCgpa(request.getCgpa());
        student.setGraduationYear(request.getGraduationYear());
        student.setSkills(request.getSkills());
        student.setRole(Role.ROLE_STUDENT);

        Student savedStudent = studentRepository.save(student);

        UserDetails userDetails = userDetailsService.loadUserByUsername(savedStudent.getEmail());
        String token = jwtUtil.generateToken(userDetails, savedStudent.getRole().name(), savedStudent.getId());

        StudentDTO studentDTO = mapToDTO(savedStudent);
        return new AuthResponse(token, studentDTO);
    }

    public AuthResponse login(AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            throw new BadRequestException("Invalid email or password");
        }

        Student student = studentRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (student.getRole() == Role.ROLE_ADMIN) {
            throw new BadRequestException("This account has administrator privileges. Please use the Admin Portal Login instead.");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(student.getEmail());
        String token = jwtUtil.generateToken(userDetails, student.getRole().name(), student.getId());

        StudentDTO studentDTO = mapToDTO(student);
        return new AuthResponse(token, studentDTO);
    }

    public AuthResponse loginAdmin(AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            throw new BadRequestException("Invalid admin credentials");
        }

        Student student = studentRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (student.getRole() != Role.ROLE_ADMIN) {
            throw new BadRequestException("Access denied: You do not have administrator permissions.");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(student.getEmail());
        String token = jwtUtil.generateToken(userDetails, student.getRole().name(), student.getId());

        StudentDTO studentDTO = mapToDTO(student);
        return new AuthResponse(token, studentDTO);
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

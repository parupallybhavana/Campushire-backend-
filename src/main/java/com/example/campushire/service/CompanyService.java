package com.example.campushire.service;

import com.example.campushire.dto.CompanyDTO;
import com.example.campushire.entity.Company;
import com.example.campushire.exception.BadRequestException;
import com.example.campushire.exception.ResourceNotFoundException;
import com.example.campushire.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    public CompanyDTO createCompany(CompanyDTO dto) {
        if (companyRepository.existsByName(dto.getName())) {
            throw new BadRequestException("Company with name '" + dto.getName() + "' already exists.");
        }

        Company company = new Company();
        company.setName(dto.getName());
        company.setDescription(dto.getDescription());
        company.setLocation(dto.getLocation());
        company.setWebsite(dto.getWebsite());

        Company saved = companyRepository.save(company);
        return mapToDTO(saved);
    }

    public List<CompanyDTO> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CompanyDTO getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
        return mapToDTO(company);
    }

    public CompanyDTO updateCompany(Long id, CompanyDTO dto) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));

        company.setName(dto.getName());
        company.setDescription(dto.getDescription());
        company.setLocation(dto.getLocation());
        company.setWebsite(dto.getWebsite());

        Company updated = companyRepository.save(company);
        return mapToDTO(updated);
    }

    public void deleteCompany(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Company not found with id: " + id);
        }
        companyRepository.deleteById(id);
    }

    public CompanyDTO mapToDTO(Company company) {
        return new CompanyDTO(
                company.getId(),
                company.getName(),
                company.getDescription(),
                company.getLocation(),
                company.getWebsite()
        );
    }
}

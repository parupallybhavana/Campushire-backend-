package com.example.campushire.dto;

import jakarta.validation.constraints.NotBlank;

public class CompanyDTO {

    private Long id;

    @NotBlank(message = "Company name is required")
    private String name;

    private String description;
    private String location;
    private String website;

    public CompanyDTO() {
    }

    public CompanyDTO(Long id, String name, String description, String location, String website) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.website = website;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}

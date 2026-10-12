package com.example.campushire.dto;

public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private StudentDTO user;

    public AuthResponse() {
    }

    public AuthResponse(String token, StudentDTO user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public StudentDTO getUser() {
        return user;
    }

    public void setUser(StudentDTO user) {
        this.user = user;
    }
}

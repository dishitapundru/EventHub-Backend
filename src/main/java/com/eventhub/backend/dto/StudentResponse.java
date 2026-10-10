
package com.eventhub.backend.dto;

import java.time.LocalDateTime;

public class StudentResponse {

    private Long id;
    private String name;
    private String email;
    private String role;
    private LocalDateTime createdAt;
    private String department;
    private String year;

    public StudentResponse(
            Long id,
            String name,
            String email,
            String role,
            LocalDateTime createdAt,
            String department,
            String year
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
        this.department = department;
        this.year = year;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getDepartment() {
        return department;
    }

    public String getYear() {
        return year;
    }
}

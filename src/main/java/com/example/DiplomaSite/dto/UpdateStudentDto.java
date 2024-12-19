package com.example.DiplomaSite.dto;

import lombok.Data;

@Data
public class UpdateStudentDto {
    private String name;
    private String facultyNumber;
    private String keycloakUserId;
}
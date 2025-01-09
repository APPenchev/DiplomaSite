package com.example.DiplomaSite.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateStudentDto {
    private String name;
    private String facultyNumber;
    private String keycloakUserId;
}
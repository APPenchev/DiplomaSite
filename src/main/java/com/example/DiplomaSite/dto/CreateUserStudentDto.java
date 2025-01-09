package com.example.DiplomaSite.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUserStudentDto {
    @NotBlank
    CreateStudentDto student;
    @NotBlank
    UserCredentials credentials;
}

package com.example.DiplomaSite.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUserStudentDto {
    CreateStudentDto student;
    UserCredentials credentials;
}

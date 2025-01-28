package com.example.DiplomaSite.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUserTeacherDto {
    private CreateTeacherDto teacher;
    private UserCredentials credentials;
}

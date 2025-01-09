package com.example.DiplomaSite.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUserTeacherDto {
    @NotBlank
    private CreateTeacherDto teacher;
    @NotBlank
    private UserCredentials credentials;
}

package com.example.DiplomaSite.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateStudentDto {
    @NotBlank
    private String name;

}
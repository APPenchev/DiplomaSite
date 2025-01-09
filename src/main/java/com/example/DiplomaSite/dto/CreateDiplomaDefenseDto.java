package com.example.DiplomaSite.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateDiplomaDefenseDto {

    @NotBlank
    private LocalDate date;

    @NotBlank
    private Long supervisorId;
}

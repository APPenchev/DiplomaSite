package com.example.DiplomaSite.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateDiplomaThesisDto {
    @NotBlank
    private String title;
    @NotBlank
    private String text;
    @NotBlank
    private LocalDate uploadDate;
    @NotBlank
    private Boolean confidential;

}

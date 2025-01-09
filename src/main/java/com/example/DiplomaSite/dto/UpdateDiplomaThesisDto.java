package com.example.DiplomaSite.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateDiplomaThesisDto {

    private String title;
    private String text;
    private LocalDate uploadDate;
    private Boolean confidential;
}

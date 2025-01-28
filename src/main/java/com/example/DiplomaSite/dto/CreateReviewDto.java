package com.example.DiplomaSite.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateReviewDto {
    @NotBlank
    private String text;

    private LocalDate uploadDate;

    private Boolean positive;

    @NotNull
    private Long teacherId;

    @NotNull
    private Long thesisId;

}

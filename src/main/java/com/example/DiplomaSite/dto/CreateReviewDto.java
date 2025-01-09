package com.example.DiplomaSite.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateReviewDto {
    @NotBlank
    private String text;

    @NotBlank
    private LocalDate uploadDate;

    @NotBlank
    private Boolean positive;

    @NotBlank
    private Long teacherId;

}

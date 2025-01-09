package com.example.DiplomaSite.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateReviewDto {
    private String text;
    private LocalDate uploadDate;
    private Boolean positive;
}

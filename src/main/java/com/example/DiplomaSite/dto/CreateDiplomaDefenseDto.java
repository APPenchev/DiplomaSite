package com.example.DiplomaSite.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateDiplomaDefenseDto {
    @NotNull
    private Long thesisId;
    @NotNull
    private LocalDate date;
    @NotNull
    private Long supervisorId;
    @NotNull
    private List<Long> committeeMembersIds;
}

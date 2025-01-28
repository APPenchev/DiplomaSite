package com.example.DiplomaSite.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateDiplomaAssignmentDto {
    @NotBlank
    private String topic;
    @NotBlank
    private String goal;
    @NotBlank
    private String tasks;
    @NotBlank
    private String technologies;
    @NotNull
    private Long studentId;
    @NotNull
    private Long supervisorId;
}
